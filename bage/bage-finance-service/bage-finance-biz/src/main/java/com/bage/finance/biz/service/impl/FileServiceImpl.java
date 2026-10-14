package com.bage.finance.biz.service.impl;

import com.bage.common.config.MinioConfig;
import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.constant.MqConstant;
import com.bage.finance.biz.constant.RedisKeyConstant;
import com.bage.finance.biz.domain.File;
import com.bage.finance.biz.domain.Member;
import com.bage.finance.biz.domain.MqMsg;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.ListFileVo;
import com.bage.finance.biz.enums.MqMsgStatusEnum;
import com.bage.finance.biz.mapper.FileMapper;
import com.bage.finance.biz.service.FileService;

import com.bage.finance.biz.service.MemberService;
import com.bage.finance.biz.service.MqMsgService;
import com.bage.mybatis.help.MyBatisWrapper;
import com.bage.mybatis.help.PageInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Sets;
import org.apache.logging.log4j.util.Strings;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bage.finance.biz.domain.FileField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    final FileMapper mapper;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;
    final ObjectMapper objectMapper;
    final ApplicationContext applicationContext;
    final MinioClient minioClient;
    final MinioConfig minioConfig;
    final MemberService memberService;
    final TransactionTemplate transactionTemplate;
    final ObjectMapper jsonMapper;
    final MqMsgService mqMsgService;
    final RocketMQTemplate rocketMQTemplate;
    final RedissonClient redissonClient;

    /**
     * 分片上传文件
     *
     * @param form
     * @return
     */
    @Override
    public String uploadPart(UploadFileForm form) throws Exception {
        // 文件大小
        long size = form.getFile().getSize();
        if (size == 0) {
            throw new BizException("禁止上传空文件");
        }

        // 文件名称
        String fileName = form.getFileName();
        if (Strings.isBlank(fileName)) {
            throw new BizException("文件名为空");
        }

        // 文件后缀
        String ext = "";

        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            throw new BizException("禁止上传无后缀的文件");
        }

        ext = fileName.substring(index);

        // 文件类型
        String contentType = form.getFile().getContentType();
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        // 根据日期打散目录，使用 UUID 重命名文件
        String filePath = UUID.randomUUID().toString().replace("-", "") + ext;
        String objectName = tokenService.getThreadLocalUserId().toString()
                .concat("/")
                .concat(form.getUid())
                .concat("/")
                .concat(form.getChunk().toString());


        log.info("文件名称：{}", fileName);
        log.info("文件大小：{}", size);
        log.info("文件类型：{}", contentType);
        log.info("文件路径：{}", filePath);

        // 上传文件到客户端
        try (InputStream inputStream = form.getFile().getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucket())        // 指定 Bucket
                    .contentType(contentType)    // 指定 Content Type
                    .object(form.getTotalChunks() == 1 ? filePath : objectName)            // 指定文件的路径
                    .stream(inputStream, size, -1) // 文件的 Inputstream 流
                    .build());
        }
        if (form.getTotalChunks() == 1) {
            File file = new File();
            file.initDefault();
            file.setFolderId(form.getFolderId());
            file.setName(form.getFileName());
            file.setSize(form.getFile().getSize());
            file.setPath(filePath);
            file.setExtension(ext);
            file.setMemberId(tokenService.getThreadLocalUserId());
            file.setUpdateMemberId(tokenService.getThreadLocalUserId());
            file.setTenantId(tokenService.getThreadLocalTenantId());
            mapper.insert(file);
            // 生成预签名的GetObjectUrl
            String getObjectUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .method(Method.GET)
                    .object(filePath)
                    .expiry(300)
                    .build());
            return getObjectUrl;
        }
        return null;
    }

    /**
     * 合并上传后的文件
     *
     * @param form
     * @return
     */
    @Override
    public String composeFile(ComposeFileForm form) {
        // 合并操作
        try {
            // 文件后缀
            String ext = "";

            int index = form.getFileName().lastIndexOf(".");
            if (index == -1) {
                throw new BizException("禁止上传无后缀的文件");
            }

            ext = form.getFileName().substring(index);

            // 根据日期打散目录，使用 UUID 重命名文件
            String filePath = UUID.randomUUID().toString().replace("-", "") + ext;
            // 完成上传从缓存目录合并迁移到正式目录
            List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                    .limit(form.getTotalChunks())
                    .map(i -> ComposeSource.builder()
                            .bucket(minioConfig.getBucket())
                            .object(tokenService.getThreadLocalUserId().toString()
                                    .concat("/")
                                    .concat(form.getUid())
                                    .concat("/")
                                    .concat(Integer.toString(i)))
                            .build())
                    .collect(Collectors.toList());

            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(minioConfig.getBucket())        // 指定 Bucket
                            .object(filePath)
                            .sources(sourceObjectList)
                            .build());
            // 生成预签名的GetObjectUrl
            String getObjectUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .method(Method.GET)
                    .object(filePath)
                    .expiry(300)
                    .build());
            //获取上传后文件的大小
            StatObjectResponse objectStat = minioClient.statObject(StatObjectArgs.builder().bucket(minioConfig.getBucket())
                    .object(filePath).build()
            );
            MqMsg mqMsg = new MqMsg();
            mqMsg.setMqTopic(MqConstant.TOPIC_FILE);
            mqMsg.setMqTag(MqConstant.TAG_DEL_FILE);
            mqMsg.setRequestId(UUID.randomUUID().toString());
            mqMsg.setMsgClassName(DelSliceFileForm.class.getName());
            DelSliceFileForm delSliceFileForm = new DelSliceFileForm();
            delSliceFileForm.setMemberId(tokenService.getThreadLocalUserId());
            delSliceFileForm.setTotalChunks(form.getTotalChunks());
            delSliceFileForm.setUid(form.getUid());
            delSliceFileForm.setMsgRequestId(mqMsg.getRequestId());
            try {
                mqMsg.setMsg(jsonMapper.writeValueAsString(delSliceFileForm));
            } catch (JsonProcessingException jsonEx) {
                throw new BizException("序列化异常", jsonEx);
            }
            File file = new File();
            file.initDefault();
            file.setFolderId(form.getFolderId());
            file.setName(form.getFileName());
            file.setSize(objectStat.size());
            file.setPath(filePath);
            file.setExtension(ext);
            file.setMemberId(tokenService.getThreadLocalUserId());
            file.setUpdateMemberId(tokenService.getThreadLocalUserId());
            file.setTenantId(tokenService.getThreadLocalTenantId());
            //执行本地事务
            transactionTemplate.execute((transactionStatus) -> {
                mapper.insert(file);
                mqMsg.setMqKey(file.getId().toString());
                mqMsgService.create(mqMsg);
                return true;
            });
            //创建mq消息
            Message<MqMsg> msg = MessageBuilder.withPayload(mqMsg)
                    .setHeader(RocketMQHeaders.KEYS, file.getId()) // 设置消息 key
                    .build();

            //通过rocketmq发送mq消息
            rocketMQTemplate.asyncSend(mqMsg.getMqTopic() + ":" + mqMsg.getMqTag(), msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    // 异步消息发送成功的处理逻辑
                    log.info("消息发送成功:{}", sendResult.toString());
                }

                @Override
                public void onException(Throwable e) {
                    // 异步消息发送失败的处理逻辑
                    log.error("消息发送失败", e);
                }
            });
            return getObjectUrl;
        } catch (Exception e) {
            log.error("Minio文件按合并异常!|参数：bucketName:{},objectName:{}|异常:{}", minioConfig.getBucket(), form.getFileName(), e);
            return null;
        }
    }

    /**
     * 删除分片文件
     *
     * @param form
     * @return
     */
    @Override
    @EventListener
    public boolean delSliceFile(DelSliceFileForm form) {
        // 删除所有的临时分片文件
        List<DeleteObject> delObjects = Stream.iterate(0, i -> ++i)
                .limit(form.getTotalChunks())
                .map(i -> new DeleteObject(form.getMemberId().toString()
                        .concat("/")
                        .concat(form.getUid())
                        .concat("/")
                        .concat(Integer.toString(i))))
                .collect(Collectors.toList());
        RLock rLock = redissonClient.getLock(RedisKeyConstant.DELETE_FILE_LOCK + form.getMsgRequestId());
        MqMsg mqMsg = null;
        try {
            rLock.lock();
            mqMsg = mqMsgService.get(form.getMsgRequestId());
            if (mqMsg.getMsgStatus().equals(MqMsgStatusEnum.SUCCESS.getCode())
                    || mqMsg.getMsgStatus().equals(MqMsgStatusEnum.FAIL.getCode())) {
                log.info("消息：{}已被消费", mqMsg.getRequestId());
                return true;
            }

            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket(minioConfig.getBucket())
                                    .objects(delObjects).build());
            boolean isFlag = true;
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("Error in deleting object {} | {}", error.objectName(), error.message());
                isFlag = false;
            }
            mqMsgService.success(form.getMsgRequestId());
            return isFlag;
        } catch (Exception e) {
            mqMsg.setErrorMsg(e.getMessage());
            mqMsgService.failByRequestId(mqMsg);
            log.error("Minio多个文件删除异常!|参数：bucketName:{},objectName:{}|异常:{}", minioConfig.getBucket(), e);
            return false;
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 查询文件列表
     *
     * @param form
     * @return
     */
    @Override
    public PageInfo<ListFileVo> list(ListFileForm form) throws Exception {
        MyBatisWrapper<File> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Name, Extension, CreateTime, MemberId, Path)
                .whereBuilder()
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        wrapper.page(form.getPageNum(), form.getPageSize());
        if (form.getFolderId() != null && form.getFolderId() > 0) {
            wrapper.and().andEq(FolderId, form.getFolderId());
        }
        if (form.getBeginDate() != null) {
            wrapper.and().andGTE(CreateTime, form.getBeginDate());
        }
        if (form.getEndDate() != null) {
            wrapper.and().andLTE(CreateTime, form.getEndDate());
        }
        if (Strings.isNotBlank(form.getName())) {
            wrapper.and().andLike(Name, "%" + form.getName() + "%");
        }
        wrapper.orderByAsc(Id);
        PageInfo<File> list = wrapper.listPage(mapper);
        PageInfo<ListFileVo> result = new PageInfo<>();
        result.setTotal(list.getTotal());
        result.setPages(list.getPages());
        result.setPageSize(list.getPageSize());
        result.setPageNum(list.getPageNum());
        result.setList(new ArrayList<>());
        if (CollectionUtils.isEmpty(list.getList())) {
            return result;
        }
        List<Member> members = memberService.listByIds(list.getList().stream().map(File::getMemberId).collect(Collectors.toSet()));
        for (File file : list.getList()) {
            ListFileVo listFileVo = objectConvertor.toListFileVo(file);
            if (!CollectionUtils.isEmpty(members)) {
                members.stream().filter(p -> p.getId().equals(file.getMemberId()))
                        .findFirst().ifPresent(p -> {
                    listFileVo.setNickName(p.getName());
                });
            }
            // 生成预签名的GetObjectUrl
            String getObjectUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .method(Method.GET)
                    .object(file.getPath())
                    .expiry(300)
                    .build());
            listFileVo.setPicUrl(getObjectUrl);
            result.getList().add(listFileVo);
        }
        return result;
    }

    /**
     * 查询文件列表
     *
     * @param form
     * @return
     * @throws Exception
     */
    @Override
    public List<ListFileVo> listByIds(ListFileByIdsForm form) throws Exception {
        List<ListFileVo> result = new ArrayList<>();

        MyBatisWrapper<File> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Name, Extension, CreateTime, MemberId, Path)
                .whereBuilder()
                .andIn(Id, form.getIds())
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());

        List<File> list = mapper.list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        List<Member> members = memberService.listByIds(list.stream().map(File::getMemberId).collect(Collectors.toSet()));
        for (File file : list) {
            ListFileVo listFileVo = objectConvertor.toListFileVo(file);
            if (!CollectionUtils.isEmpty(members)) {
                members.stream().filter(p -> p.getId().equals(file.getMemberId()))
                        .findFirst().ifPresent(p -> {
                    listFileVo.setNickName(p.getName());
                });
            }
            // 生成预签名的GetObjectUrl
            String getObjectUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .method(Method.GET)
                    .object(file.getPath())
                    .expiry(300)
                    .build());
            listFileVo.setPicUrl(getObjectUrl);
            result.add(listFileVo);
        }
        return result;
    }

    /**
     * 获取图片url
     *
     * @param id
     * @return
     */
    @Override
    public String getPicUrl(long id) throws Exception {
        MyBatisWrapper<File> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, Name, Extension, CreateTime, MemberId, Path)
                .whereBuilder()
                .andEq(Id, id)
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        File file = mapper.get(wrapper);
        if (file == null) {
            throw new BizException("文件不存在");
        }
        // 生成预签名的GetObjectUrl
        String getObjectUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(minioConfig.getBucket())
                .method(Method.GET)
                .object(file.getPath())
                .expiry(300)
                .build());
        return getObjectUrl;
    }

    /**
     * 删除图片
     *
     * @param form
     * @return
     */
    @Override
    public boolean del(DelForm form) {
        MyBatisWrapper<File> wrapper = new MyBatisWrapper<>();
        wrapper.update(DelFlag, true)
                .update(UpdateTime, DateUtil.getSystemTime())
                .whereBuilder()
                .andEq(Id, form.getId())
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());

        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("删除失败");
        }
        return true;
    }

    /**
     * 检测文件id是否合法
     *
     * @param ids
     * @return
     */
    @Override
    public int checkFileIds(Set<Long> ids, long tenantId) {
        MyBatisWrapper<File> wrapper = new MyBatisWrapper<>();
        wrapper.whereBuilder()
                .andIn(Id, ids)
                .andEq(DelFlag, false)
                .andEq(TenantId, tenantId);
        return mapper.count(wrapper);
    }

    /**
     * 统计某个文件夹文件的数量
     *
     * @param folderId
     * @return
     */
    @Override
    public int countByFolderId(long folderId) {
        MyBatisWrapper<File> wrapper = new MyBatisWrapper<>();
        wrapper.whereBuilder()
                .andEq(FolderId, folderId)
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return mapper.count(wrapper);
    }

    /**
     * 获取桶中的文件对象
     *
     * @param bucketName       桶名称
     * @param prefixObjectName 前缀对象名称
     */
    private Set<String> getFileObjects(String bucketName, String prefixObjectName) {
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName)
                            .prefix(prefixObjectName.concat("/")).build());
            Set<String> objectNames = Sets.newHashSet();
            for (Result<Item> item : results) {
                objectNames.add(item.get().objectName());
            }
            return objectNames;
        } catch (Exception e) {
            log.error("Minio获取文件流异常!|参数：bucketName:{},prefixObjectName:{}|异常:{}", bucketName, prefixObjectName, e);
        }
        return null;
    }
}
