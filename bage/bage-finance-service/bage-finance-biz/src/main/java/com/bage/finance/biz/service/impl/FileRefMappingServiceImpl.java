package com.bage.finance.biz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.finance.biz.constant.RedisKeyConstant;
import com.bage.finance.biz.domain.FileRefMapping;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.ListFileRefMappingFileIdsForm;
import com.bage.finance.biz.enums.FileRefTypeEnum;
import com.bage.finance.biz.mapper.FileRefMappingMapper;
import com.bage.finance.biz.service.FileRefMappingService;
import com.bage.mybatis.help.MyBatisWrapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bage.finance.biz.domain.FileRefMappingField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileRefMappingServiceImpl implements FileRefMappingService {
    final FileRefMappingMapper mapper;
    final TokenService<AdminDTO> tokenService;
    final RedissonClient redissonClient;
    final TransactionTemplate transactionTemplate;

    /**
     * 保存文件映射
     *
     * @param fileRefTypeEnum 引用类型
     * @param fileRefId       引用id
     * @param fileIds         映射的文件id列表
     * @return
     */
    @Override
    public boolean save(FileRefTypeEnum fileRefTypeEnum, long fileRefId, Set<Long> fileIds,
                        long tenantId,
                        long memberId) {
        //如果前端传的文件id是null，则不做任何处理，表示文件没有变化，如果不是null，但数组长度为0表示删除所有文件映射
        if (fileIds == null) {
            return true;
        }
        //要插入的文件映射
        Set<Long> insertFileIds = null;
        //要删除的文件映射
        Set<Long> delFileIds = null;
        RLock rLock = redissonClient.getLock(RedisKeyConstant.SAVE_FILE_REF_MAPPING_LOCK +
                fileRefTypeEnum.getCode() + ":" + fileRefId);
        try {
            rLock.lock();
            List<FileRefMapping> fileRefMappings = list(fileRefTypeEnum, fileRefId, tenantId);
            //如果文件id为空，则删除所有
            if (!CollectionUtils.isEmpty(fileRefMappings)) {
                Set<Long> fileIdsByDb = fileRefMappings.stream().map(FileRefMapping::getFileId)
                        .collect(Collectors.toSet());
                //如果映射文件id为空则删除所有映射
                if (CollectionUtils.isEmpty(fileIds)) {
                    MyBatisWrapper<FileRefMapping> wrapper = new MyBatisWrapper<>();
                    wrapper.update(DelFlag, true)
                            .update(UpdateTime, DateUtil.getSystemTime())
                            .update(UpdateMemberId, memberId)
                            .whereBuilder()
                            .andEq(RefType, fileRefTypeEnum.getCode())
                            .andEq(RefId, fileRefId)
                            .andEq(TenantId, tenantId)
                            .andEq(DelFlag, false);
                    if (mapper.updateField(wrapper) == 0) {
                        throw new BizException("删除文件映射失败");
                    }
                    return true;
                }

                //找出要插入的的映射
                insertFileIds = fileIds.stream().filter(fileId -> !fileIdsByDb.contains(fileId)).collect(Collectors.toSet());
                //找出要删除的的映射
                delFileIds = fileIdsByDb.stream().filter(fileId -> !fileIds.contains(fileId)).collect(Collectors.toSet());
            }
            if (CollectionUtils.isEmpty(fileRefMappings)) {
                //无需保存
                if (CollectionUtils.isEmpty(fileIds)) {
                    return true;
                }
                //前端传入的都是需要插入的文件映射
                insertFileIds = fileIds;
            }
            // 如果既没有要插入的也没有要删除的 则无效做任何变更，直接返回true
            if (CollectionUtils.isEmpty(insertFileIds) && CollectionUtils.isEmpty(delFileIds)) {
                return true;
            }

            List<FileRefMapping> fileRefMappingList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(insertFileIds)) {
                insertFileIds.forEach(fileId -> {
                    FileRefMapping fileRefMapping = new FileRefMapping();
                    fileRefMapping.initDefault();
                    fileRefMapping.setRefType(fileRefTypeEnum.getCode());
                    fileRefMapping.setRefId(fileRefId);
                    fileRefMapping.setFileId(fileId);
                    fileRefMapping.setTenantId(tenantId);
                    fileRefMapping.setMemberId(memberId);
                    fileRefMapping.setUpdateMemberId(memberId);
                    fileRefMappingList.add(fileRefMapping);
                });
            }

            final Set<Long> finalDelFileIds = delFileIds;
            MyBatisWrapper<FileRefMapping> delWrapper = new MyBatisWrapper<>();
            if (!CollectionUtils.isEmpty(finalDelFileIds)) {
                delWrapper.update(DelFlag, true)
                        .update(UpdateTime, DateUtil.getSystemTime())
                        .update(MemberId, memberId)
                        .whereBuilder()
                        .andEq(RefType, fileRefTypeEnum.getCode())
                        .andEq(RefId, fileRefId)
                        .andIn(FileId, finalDelFileIds)
                        .andEq(TenantId, tenantId)
                        .andEq(DelFlag, false);
            }

            transactionTemplate.execute((transactionStatus) -> {
                if (!CollectionUtils.isEmpty(finalDelFileIds)) {
                    if (mapper.updateField(delWrapper) == 0) {
                        throw new BizException("删除文件映射失败");
                    }
                }
                if (!CollectionUtils.isEmpty(fileRefMappingList)) {
                    mapper.insertBatch(fileRefMappingList);
                }
                return true;
            });
            return true;
        } catch (Exception ex) {
            throw new BizException("保存文件映射异常", ex);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 获取文件id列表
     *
     * @param form
     * @return
     */
    @Override
    public Set<Long> listFileIds(ListFileRefMappingFileIdsForm form) {
        List<FileRefMapping> fileRefMappings = list(FileRefTypeEnum.getEnum(form.getFileRefType()), form.getFileRefId(),
                tokenService.getThreadLocalTenantId());
        if (!CollectionUtils.isEmpty(fileRefMappings)) {
            return fileRefMappings.stream()
                    .map(FileRefMapping::getFileId).collect(Collectors.toSet());
        }
        return null;
    }

    /**
     * 根据引用类型也引用id查询文件引用映射列表
     *
     * @param fileRefTypeEnum
     * @param fileRefId
     * @return
     */
    private List<FileRefMapping> list(FileRefTypeEnum fileRefTypeEnum,
                                      long fileRefId,
                                      long tenantId) {
        MyBatisWrapper<FileRefMapping> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, RefType, RefId, FileId)
                .whereBuilder()
                .andEq(RefType, fileRefTypeEnum.getCode())
                .andEq(RefId, fileRefId)
                .andEq(TenantId, tenantId)
                .andEq(DelFlag, false);
        return mapper.list(wrapper);
    }

}