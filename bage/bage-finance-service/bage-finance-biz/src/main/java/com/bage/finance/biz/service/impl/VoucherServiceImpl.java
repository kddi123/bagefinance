package com.bage.finance.biz.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.bage.common.exception.BizException;
import com.bage.common.service.TokenService;
import com.bage.common.util.DateUtil;
import com.bage.finance.biz.config.ObjectConvertor;
import com.bage.finance.biz.constant.MqConstant;
import com.bage.finance.biz.constant.RedisKeyConstant;
import com.bage.finance.biz.domain.*;
import com.bage.finance.biz.domain.es.VoucherDocuemt;
import com.bage.finance.biz.dto.AdminDTO;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.GetSubjectVo;
import com.bage.finance.biz.dto.vo.GetVoucherVo;
import com.bage.finance.biz.dto.vo.ListCurrencyConfigVo;
import com.bage.finance.biz.dto.vo.ListVoucherVo;
import com.bage.finance.biz.enums.FileRefTypeEnum;
import com.bage.finance.biz.enums.ListVoucherSortFieldEnum;
import com.bage.finance.biz.enums.MqMsgStatusEnum;
import com.bage.finance.biz.mapper.VoucherMapper;
import com.bage.finance.biz.mapper.VoucherSubjectAssistCalculateDetailMapper;
import com.bage.finance.biz.mapper.VoucherSubjectDetailMapper;
import com.bage.finance.biz.service.*;
import com.bage.mybatis.help.MyBatisWrapper;
import com.bage.mybatis.help.PageInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.bage.finance.biz.domain.VoucherField.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    final VoucherMapper mapper;
    final VoucherSubjectDetailMapper voucherSubjectDetailMapper;
    final VoucherSubjectDetailService voucherSubjectDetailService;

    final VoucherSubjectAssistCalculateDetailMapper voucherSubjectAssistCalculateDetailMapper;

    final VoucherSubjectAssistCalculateDetailService voucherSubjectAssistCalculateDetailService;

    final ObjectConvertor objectConvertor;
    final TransactionTemplate transactionTemplate;
    final SubjectService subjectService;
    final CurrencyConfigService currencyConfigService;
    final AssistCalculateSummaryService assistCalculateSummaryService;
    final TokenService<AdminDTO> tokenService;
    final VoucherWordConfigService voucherWordConfigService;
    final AssistCalculateCateService assistCalculateCateService;
    final ObjectMapper jsonMapper;
    final RedissonClient redissonClient;
    final RocketMQTemplate rocketMQTemplate;
    final ElasticsearchClient elasticsearchClient;
    final MqMsgService mqMsgService;
    final FileService fileService;
    final FileRefMappingService fileRefMappingService;

    /**
     * 创建或保存凭证
     *
     * @param form
     * @return
     */
    @Override
    public boolean save(CreateVoucherForm form) {
        //# region 前端参数校验
        //判断科目行编号是否重复，行编号主要是为了让前端知道哪一行报错了
        boolean hasDuplicateRow = form.getVoucherSubjectDetailFormList().stream()
                .map(CreateVoucherForm.VoucherSubjectDetailForm::getRowNo)
                .collect(Collectors.toSet())
                .size() != form.getVoucherSubjectDetailFormList().size();
        if (hasDuplicateRow) {
            throw new BizException("行编号重复");
        }

        //对凭证借方金额求和
        BigDecimal sumDebitAmount = form.getVoucherSubjectDetailFormList().stream().map(CreateVoucherForm.VoucherSubjectDetailForm::getDebitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //对凭证贷方金额求和
        BigDecimal sumCreditAmount = form.getVoucherSubjectDetailFormList().stream().map(CreateVoucherForm.VoucherSubjectDetailForm::getCreditAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sumDebitAmount.compareTo(sumCreditAmount) != 0) {
            throw new BizException("借贷不平衡");
        }

        Voucher voucher = objectConvertor.toVoucher(form);
        voucher.initDefault();
        voucher.setTenantId(tokenService.getThreadLocalTenantId());

        List<VoucherSubjectDetail> voucherSubjectDetailList = objectConvertor.toVoucherSubjectDetail(
                form.getVoucherSubjectDetailFormList());
        List<VoucherSubjectAssistCalculateDetail> voucherSubjectAssistCalculateDetailList =
                objectConvertor.toVoucherSubjectAssistCalculateDetail(form.getVoucherSubjectAssistCalculateDetailFormList());

        //查询前端传的所有科目id列表
        Set<Long> subjectIds = form.getVoucherSubjectDetailFormList().stream().map(CreateVoucherForm.VoucherSubjectDetailForm::getSubjectId)
                .collect(Collectors.toSet());
        //# endregion 前端参数校验
        ThreadPoolExecutor executor = null;
        //租户id
        long tenantId = tokenService.getThreadLocalTenantId();
        RLock rLock = redissonClient.getLock(RedisKeyConstant.SAVE_VOUCHER_LOCK + form.getVoucherNumber());
        try {
            rLock.lock();
            //必须的任务（凭证号是否重复、查询币别列表、科目明细查询）
            //需要查询凭证和凭证科目明细，任务数+2
            //最少有3个任务，最多7个
            int taskCount = 7;
            executor = new ThreadPoolExecutor(
                    taskCount,  // 核心线程池大小
                    taskCount,  // 最大线程池大小
                    60, // 线程空闲时间
                    TimeUnit.SECONDS, // 时间单位
                    new LinkedBlockingQueue<>(taskCount)); // 任务队列

            Future<Boolean> finalExistByVoucherNumberRef;
            finalExistByVoucherNumberRef = executor.submit(() -> isExistByVoucherNumber(form.getId(), form.getVoucherNumber(), tenantId));

            //通过id查询凭证
            Future<Voucher> finalVoucherRef = null;
            //凭证科目明细
            Future<List<VoucherSubjectDetail>> finalVoucherSubjectDetailsRef = null;
            if (form.getId() != null && form.getId() > 0) {
                //通过id查询凭证
                finalVoucherRef = executor.submit(() -> {
                    Voucher voucher1 = getById(form.getId());
                    if (voucher1.getTenantId() != tenantId) {
                        return null;
                    }
                    return voucher1;
                });

                //通过id查询凭证科目明细
                finalVoucherSubjectDetailsRef = executor.submit(() -> voucherSubjectDetailService.listByVoucherId(form.getId()));
            }

            Future<List<GetSubjectVo>> finalGetSubjectVoRef;
            finalGetSubjectVoRef = executor.submit(() -> subjectService.list(subjectIds, tenantId));

            Future<List<ListCurrencyConfigVo>> finalCurrencyConfigVoListRef;
            finalCurrencyConfigVoListRef = executor.submit(() -> currencyConfigService.list(tenantId));

            //辅助核算
            Future<List<AssistCalculateSummary>> finalAssistCalculateSummaryListRef = null;
            if (!CollectionUtils.isEmpty(form.getVoucherSubjectAssistCalculateDetailFormList())) {
                //统计前端传递的所有辅助核算id列表
                Set<Long> assistCalculateIds =
                        form.getVoucherSubjectAssistCalculateDetailFormList().stream()
                                .map(CreateVoucherForm.VoucherSubjectAssistCalculateDetailForm::getAssistCalculateId)
                                .collect(Collectors.toSet());
                finalAssistCalculateSummaryListRef = executor.submit(() -> assistCalculateSummaryService.list(assistCalculateIds, tenantId));
            }

            //文件id校验
            Future<Integer> finalFileCountRef = null;
            if (!CollectionUtils.isEmpty(form.getFileIds())) {
                finalFileCountRef = executor.submit(() ->
                        fileService.checkFileIds(form.getFileIds(), tenantId));
            }

            //获取科目编号是否已存在
            boolean isExistByVoucherNumber = finalExistByVoucherNumberRef.get();
            if (isExistByVoucherNumber) {
                throw new BizException("凭证编号已存在");
            }
            // 获取凭证
            Voucher finalVoucher = finalVoucherRef != null ? finalVoucherRef.get() : null;
            if (form.getId() != null && form.getId() > 0 && finalVoucher == null) {
                throw new BizException("凭证不存在");
            }
            //凭证科目明细列表
            List<VoucherSubjectDetail> finalVoucherSubjectDetails = finalVoucherSubjectDetailsRef != null ? finalVoucherSubjectDetailsRef.get() : null;
            if (finalVoucher != null && CollectionUtils.isEmpty(finalVoucherSubjectDetails)) {
                throw new BizException("凭证科目明细不存在");
            }
            //获取科目列表
            List<GetSubjectVo> getSubjectVoList = finalGetSubjectVoRef.get();
            if (CollectionUtils.isEmpty(getSubjectVoList)) {
                throw new BizException("科目不存在");
            }
            if (getSubjectVoList.size() != subjectIds.size()) {
                throw new BizException("科目参数非法");
            }
            //获取币别列表
            List<ListCurrencyConfigVo> currencyConfigVoList = finalCurrencyConfigVoListRef.get();
            if (CollectionUtils.isEmpty(currencyConfigVoList)) {
                throw new BizException("币别不存在");
            }
            //辅助核算列表
            List<AssistCalculateSummary> finalAssistCalculateSummaryList = finalAssistCalculateSummaryListRef != null ? finalAssistCalculateSummaryListRef.get() : null;
            if (!CollectionUtils.isEmpty(form.getVoucherSubjectAssistCalculateDetailFormList())
                    && CollectionUtils.isEmpty(finalAssistCalculateSummaryList)) {
                throw new BizException("辅助核算为空");
            }
            // 获取文件数量
            int finalFileCount = finalFileCountRef != null ? finalFileCountRef.get() : 0;
            if (!CollectionUtils.isEmpty(form.getFileIds()) && finalFileCount != form.getFileIds().size()) {
                throw new BizException("文件不存在");
            }
            //遍历前端传递的科目明细
            for (CreateVoucherForm.VoucherSubjectDetailForm voucherSubjectDetailForm : form.getVoucherSubjectDetailFormList()) {
                GetSubjectVo getSubjectVo = getSubjectVoList.stream().filter(p -> p.getId().equals(voucherSubjectDetailForm.getSubjectId())).findFirst()
                        .orElse(null);
                if (getSubjectVo == null) {
                    throw new BizException("获取科目失败");
                }

                VoucherSubjectDetail voucherSubjectDetail = voucherSubjectDetailList.stream().filter(p -> p.getRowNo().equals(voucherSubjectDetailForm.getRowNo()))
                        .findFirst().orElseThrow(() -> new BizException("获取凭证科目明细失败"));

                if ((voucherSubjectDetailForm.getDebitAmount() == null || voucherSubjectDetailForm.getDebitAmount().equals(BigDecimal.ZERO))
                        && (voucherSubjectDetailForm.getCreditAmount() == null || voucherSubjectDetailForm.getCreditAmount().equals(BigDecimal.ZERO))) {
                    throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "行，请输入借方或贷方金额！");
                }
                //前端传的是否是借方金额
                boolean isDebitAmount = false;
                //前端传的是否是贷方金额
                boolean isCreditAmount = false;
                //设置科目金额初始值
                BigDecimal subjectAmount = BigDecimal.ZERO;

                //如果借方金额不为空则将借方金额设置为该科目的金额
                if (voucherSubjectDetailForm.getDebitAmount() != null && !voucherSubjectDetailForm.getDebitAmount().equals(BigDecimal.ZERO)) {
                    isDebitAmount = true;
                    subjectAmount = voucherSubjectDetailForm.getDebitAmount();
                }

                //如果贷方金额不为空则将贷方金额设置为该科目的金额
                if (voucherSubjectDetailForm.getCreditAmount() != null && !voucherSubjectDetailForm.getCreditAmount().equals(BigDecimal.ZERO)) {
                    isCreditAmount = true;
                    subjectAmount = voucherSubjectDetailForm.getCreditAmount();
                }
                //表示借方金额和贷方金额都传值了，因为借方和贷方金额只能传一个，所以此处抛异常
                if (isDebitAmount && isCreditAmount) {
                    throw new BizException("借方金额和贷方金额二选一");
                }

                //# region 是否启用了数量核算
                if (getSubjectVo.getSubjectCalculateConfigVo().getEnableNumberCalculateConfig()) {
                    if (voucherSubjectDetailForm.getSubjectNum() == null
                            || voucherSubjectDetailForm.getSubjectNum() == 0
                            || voucherSubjectDetailForm.getSubjectUnitPrice() == null
                            || voucherSubjectDetailForm.getSubjectUnitPrice().equals(BigDecimal.ZERO)) {
                        throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "行，请填写数量和单价");
                    }
                    //重新赋值科目金额，此时科目金额为单价*数量
                    BigDecimal price = voucherSubjectDetailForm.getSubjectUnitPrice()
                            .multiply(new BigDecimal(voucherSubjectDetailForm.getSubjectNum().toString()))
                            //四舍五入保留两位小数
                            .setScale(2, RoundingMode.HALF_UP);
                    if (subjectAmount.compareTo(price) != 0) {
                        throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "行，数量*单价和借方或贷方金额不匹配！");
                    }
                } else {
                    //如果未启用数量核算则相关参数不能填写
                    if (voucherSubjectDetailForm.getSubjectNum() != null
                            || voucherSubjectDetailForm.getSubjectUnitPrice() != null) {
                        throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "行，未启用数量核算，不能填写相关参数");
                    }
                }
                //# endregion 是否启用了数量核算

                //# region外币核算相关
                if (getSubjectVo.getSubjectCalculateConfigVo().getEnableForeignCurrencyConfig()) {
                    //前端必须选择一个币别
                    if (voucherSubjectDetailForm.getCurrencyConfigId() == null) {
                        throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "行，请设置币别");
                    }
                    //前端必须设置汇率
                    if (voucherSubjectDetailForm.getExchangeRate() == null
                            || voucherSubjectDetailForm.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "行，请输入汇率且不能小于或等于0！");
                    }
                    //前端必须设置原币
                    if (voucherSubjectDetailForm.getOriginalCurrency() == null
                            || voucherSubjectDetailForm.getOriginalCurrency().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "行，没有设置原币,且需大于0");
                    }
                    //汇率*原币=借款金额/贷款金额
                    BigDecimal price = voucherSubjectDetailForm.getExchangeRate().multiply(voucherSubjectDetailForm.getOriginalCurrency())
                            //四舍五入保留两位小数
                            .setScale(2, RoundingMode.HALF_UP);
                    if (price.compareTo(subjectAmount) != 0) {
                        throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "行，汇率*原币和借贷金额不匹配！");
                    }
                    //校验科目设置的币别和前端传的币别是否匹配
                    getSubjectVo.getSubjectCalculateConfigVo().getForeignCurrencyConfig().getCurrencyConfigIds().stream()
                            .filter(p -> Objects.equals(p, voucherSubjectDetailForm.getCurrencyConfigId())).findFirst()
                            .orElseThrow(() -> new BizException("第" + voucherSubjectDetailForm.getRowNo() + "行，科目没有启用该币别"));

                    //查看币别信息
                    ListCurrencyConfigVo listCurrencyConfigVo = currencyConfigVoList.stream()
                            .filter(p -> Objects.equals(p.getId(), voucherSubjectDetailForm.getCurrencyConfigId())).findFirst()
                            .orElseThrow(() -> new BizException("第" + voucherSubjectDetailForm.getRowNo() + "行，币别不存在"));

                    //如果是本位币那前端必须传1
                    if (listCurrencyConfigVo.getBaseCurrencyFlag() && !voucherSubjectDetailForm.getExchangeRate().equals(BigDecimal.ONE)) {
                        throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "行，本位币汇率必须是1");
                    }
                } else {
                    //未启用外币核算且不是本位币
                    if (voucherSubjectDetailForm.getCurrencyConfigId() != null
                            || voucherSubjectDetailForm.getExchangeRate() != null
                            || voucherSubjectDetailForm.getOriginalCurrency() != null) {
                        throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "行，未启用外币核算,不能设置相关参数");
                    }
                }
                // #endregion 外币核算相关

                //#region 是否启用辅助核算
                if (getSubjectVo.getSubjectCalculateConfigVo().getEnableAssistCalculateConfigs()) {
                    //遍历当前科目下辅助核算类别配置
                    getSubjectVo.getSubjectCalculateConfigVo().getAssistCalculateConfigs().forEach(p -> {
                        if (p.getRequiredFlag() && CollectionUtils.isEmpty(form.getVoucherSubjectAssistCalculateDetailFormList())) {
                            throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "没有设置辅助核算");
                        }
                        //查询前端传递的科目某个辅助核算类别下的辅助核算列表
                        List<CreateVoucherForm.VoucherSubjectAssistCalculateDetailForm> voucherSubjectAssistCalculateDetailForms
                                = form.getVoucherSubjectAssistCalculateDetailFormList().stream()
                                .filter(m -> Objects.equals(voucherSubjectDetailForm.getRowNo(), m.getRowNo()) && m.getSubjectId().equals(getSubjectVo.getId())
                                        && m.getAssistCalculateCateId().equals(p.getAssistCalculateId())).collect(Collectors.toList());
                        if (p.getRequiredFlag() && CollectionUtils.isEmpty(voucherSubjectAssistCalculateDetailForms)) {
                            throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "没有设置辅助核算");
                        }
                        //校验辅助核算是否合法（一个科目下面辅助核算不能重复）
                        if (!CollectionUtils.isEmpty(voucherSubjectAssistCalculateDetailForms) && voucherSubjectAssistCalculateDetailForms.size() > 1) {
                            throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "参数非法");
                        }
                        //如果前端设置了辅助核算，则需要校验所设置的辅助核算是否合法
                        if (!CollectionUtils.isEmpty(voucherSubjectAssistCalculateDetailForms)) {
                            if (CollectionUtils.isEmpty(finalAssistCalculateSummaryList)) {
                                throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "没有找到辅助核算");
                            }
                            voucherSubjectAssistCalculateDetailForms.forEach(item -> {
                                AssistCalculateSummary assistCalculateSummary = finalAssistCalculateSummaryList.stream()
                                        .filter(sp -> sp.getId().equals(item.getAssistCalculateId())
                                                && sp.getAssistCalculateCateId().equals(item.getAssistCalculateCateId()))
                                        .findFirst().orElse(null);
                                if (assistCalculateSummary == null) {
                                    throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "没有找到辅助核算");
                                }
                                item.setAssistCalculateCateId(assistCalculateSummary.getAssistCalculateCateId());
                            });
                        }
                    });
                } else {
                    //如果未启用辅助核算且前端传递了辅助核算相关设置则报异常
                    if (!CollectionUtils.isEmpty(form.getVoucherSubjectAssistCalculateDetailFormList())
                            && form.getVoucherSubjectAssistCalculateDetailFormList().stream()
                            .anyMatch(m -> Objects.equals(voucherSubjectDetailForm.getRowNo(), m.getRowNo()))) {
                        throw new BizException("第" + voucherSubjectDetailForm.getRowNo() + "辅助核算已禁用，参数非法");
                    }
                }
                //#endregion 是否启用辅助核算相关

                //设置要是否启用相关配置，并保存至数据库
                voucherSubjectDetail.setEnableAssistCalculateConfigs(getSubjectVo.getSubjectCalculateConfigVo().getEnableAssistCalculateConfigs());
                voucherSubjectDetail.setEnableForeignCurrencyConfig(getSubjectVo.getSubjectCalculateConfigVo().getEnableForeignCurrencyConfig());
                voucherSubjectDetail.setEnableNumberCalculateConfig(getSubjectVo.getSubjectCalculateConfigVo().getEnableNumberCalculateConfig());
            }

            //# region 计算科目，币别，辅助核算类别，辅助核算引用计数（这样计算最终要扣减的数量有利于减少db的操作次数，提升性能）
            //科目计数（增加）
            Map<Long, Long> subjectIdCount = form.getVoucherSubjectDetailFormList().stream()
                    .collect(Collectors.groupingBy(CreateVoucherForm.VoucherSubjectDetailForm::getSubjectId,
                            Collectors.counting()));

            //币别计数（增加）
            Map<Long, Long> currencyConfigIdCount = form.getVoucherSubjectDetailFormList().stream()
                    .filter(p -> p.getCurrencyConfigId() != null && p.getCurrencyConfigId() > 0)
                    .collect(Collectors.groupingBy(CreateVoucherForm.VoucherSubjectDetailForm::getCurrencyConfigId,
                            Collectors.counting()));

            //辅助核算类别和辅助核算计数计数
            Map<Long, Long> assistCalculateCateIdCount = new HashMap<>();
            Map<Long, Long> assistCalculateIdCount = new HashMap<>();
            if (!CollectionUtils.isEmpty(form.getVoucherSubjectAssistCalculateDetailFormList())) {
                //辅助核算类别计数
                assistCalculateCateIdCount = form.getVoucherSubjectAssistCalculateDetailFormList().stream()
                        .collect(Collectors.groupingBy(CreateVoucherForm.VoucherSubjectAssistCalculateDetailForm::getAssistCalculateCateId,
                                Collectors.counting()));

                //辅助核算计数
                assistCalculateIdCount = form.getVoucherSubjectAssistCalculateDetailFormList().stream()
                        .collect(Collectors.groupingBy(CreateVoucherForm.VoucherSubjectAssistCalculateDetailForm::getAssistCalculateId,
                                Collectors.counting()));
            }
            Map<Long, Long> finalAssistCalculateCateIdCount = assistCalculateCateIdCount;
            Map<Long, Long> finalAssistCalculateIdCount = assistCalculateIdCount;
            if (finalVoucher != null) {
                //需要扣减的科目引用计数
                Map<Long, Long> deductSubjectIdCount = finalVoucherSubjectDetails.stream()
                        .collect(Collectors.groupingBy(VoucherSubjectDetail::getSubjectId,
                                Collectors.counting()));
                deductSubjectIdCount.forEach((id, count) -> {
                    Long addCount = subjectIdCount.get(id);
                    if (addCount == null) {
                        subjectIdCount.put(id, -count);
                    } else {
                        subjectIdCount.put(id, addCount - count);
                    }
                });

                //需要扣减的币别使用计数
                Map<Long, Long> deductCurrencyConfigIdCount = finalVoucherSubjectDetails.stream()
                        .filter(p -> p.getCurrencyConfigId() != null && p.getCurrencyConfigId() > 0)
                        .collect(Collectors.groupingBy(VoucherSubjectDetail::getCurrencyConfigId,
                                Collectors.counting()));
                if (!CollectionUtils.isEmpty(deductCurrencyConfigIdCount)) {
                    deductCurrencyConfigIdCount.forEach((id, count) -> {
                        Long addCount = currencyConfigIdCount.get(id);
                        if (addCount == null) {
                            currencyConfigIdCount.put(id, -count);
                        } else {
                            currencyConfigIdCount.put(id, addCount - count);
                        }
                    });
                }

                //需要扣减的辅助核算类别和辅助核算计数
                if (!CollectionUtils.isEmpty(finalAssistCalculateSummaryList)) {
                    Map<Long, Long> deductAssistCalculateCateIdCount = finalAssistCalculateSummaryList.stream()
                            .collect(Collectors.groupingBy(AssistCalculateSummary::getAssistCalculateCateId,
                                    Collectors.counting()));
                    deductAssistCalculateCateIdCount.forEach((id, count) -> {
                        Long addCount = finalAssistCalculateCateIdCount.get(id);
                        if (addCount == null) {
                            finalAssistCalculateCateIdCount.put(id, -count);
                        } else {
                            finalAssistCalculateCateIdCount.put(id, addCount - count);
                        }
                    });

                    Map<Long, Long> deductAssistCalculateIdCount = finalAssistCalculateSummaryList.stream()
                            .collect(Collectors.groupingBy(AssistCalculateSummary::getId,
                                    Collectors.counting()));
                    deductAssistCalculateIdCount.forEach((id, count) -> {
                        Long addCount = finalAssistCalculateIdCount.get(id);
                        if (addCount == null) {
                            finalAssistCalculateIdCount.put(id, -count);
                        } else {
                            finalAssistCalculateIdCount.put(id, addCount - count);
                        }
                    });
                }
            }
            //# endregion 计算科目，币别，辅助核算类别，辅助核算引用计数（这样计算最终要扣减的数量有利于减少db的操作次数，提升性能）

            MqMsg mqMsg = new MqMsg();
            mqMsg.setMqTopic(MqConstant.TOPIC_VOUCHER);
            mqMsg.setMqTag(MqConstant.TAG_SAVE_VOUCHER);
            mqMsg.setRequestId(UUID.randomUUID().toString());
            mqMsg.setMsgClassName(SaveVoucherMqForm.class.getName());
            mqMsg.setDataNo(form.getVoucherNumber().toString());
            SaveVoucherMqForm saveVoucherMqForm = new SaveVoucherMqForm();

            //执行本地事务
            transactionTemplate.execute((transactionStatus) -> {
                if (finalVoucher == null) {
                    //创建凭证
                    mapper.insert(voucher);
                    voucherWordConfigService.addUseCount(voucher.getVoucherWordConfigId(), 1);
                } else {
                    //更新凭证
                    update(form, finalVoucher.getVersion());
                    voucherSubjectDetailService.del(form.getId());
                    voucherSubjectAssistCalculateDetailService.del(form.getId());
                    //如果修改了凭证字，则删除原来的凭证字引用计数，并添加新的凭证引用计数
                    if (!finalVoucher.getVoucherWordConfigId().equals(form.getVoucherWordConfigId())) {
                        //解除原来的引用计数
                        voucherWordConfigService.deductUseCount(finalVoucher.getVoucherWordConfigId(), 1);
                        //增加新的引用计数
                        voucherWordConfigService.addUseCount(form.getVoucherWordConfigId(), 1);
                    }
                }
                voucherSubjectDetailList.forEach(p -> {
                    p.initDefault();
                    p.setVoucherId(voucher.getId());
                    p.setTenantId(tokenService.getThreadLocalTenantId());
                });
                //批量插入科目明细
                voucherSubjectDetailMapper.insertBatch(voucherSubjectDetailList);

                //批量插入科目辅助核算明细
                if (!CollectionUtils.isEmpty(voucherSubjectAssistCalculateDetailList)) {
                    voucherSubjectDetailList.forEach(p -> {
                                voucherSubjectAssistCalculateDetailList.stream()
                                        .filter(m ->
                                                Objects.equals(p.getRowNo(), m.getRowNo()))
                                        .forEach(m -> {
                                            m.initDefault();
                                            m.setVoucherId(p.getVoucherId());
                                            m.setVoucherSubjectDetailId(p.getId());
                                            m.setTenantId(tokenService.getThreadLocalTenantId());
                                        });
                            }
                    );
                    voucherSubjectAssistCalculateDetailMapper.insertBatch(voucherSubjectAssistCalculateDetailList);
                }

                //# region 增加或扣减相关引用计数

                //增加或扣减科目引用计数
                subjectIdCount.forEach((id, count) -> {
                    if (count > 0) {
                        //增加科目引用计数
                        subjectService.addUseCount(id, count.intValue());
                    }
                    if (count < 0) {
                        //扣减科目引用计数
                        subjectService.deductUseCount(id, -count.intValue());
                    }
                });

                //增加或扣减币别引用计数
                if (!CollectionUtils.isEmpty(currencyConfigIdCount)) {
                    currencyConfigIdCount.forEach((id, count) -> {
                        if (count > 0) {
                            //增加币别引用计数
                            currencyConfigService.addUseCount(id, count.intValue());
                        }
                        if (count < 0) {
                            //扣减币别引用计数
                            currencyConfigService.decrUseCount(id, -count.intValue());
                        }
                    });
                }

                //增加或扣减辅助核算类别引用计数
                if (!CollectionUtils.isEmpty(form.getVoucherSubjectAssistCalculateDetailFormList())) {
                    finalAssistCalculateCateIdCount.forEach((id, count) -> {
                        if (count > 0) {
                            //增加辅助核算类别使用计数
                            assistCalculateCateService.addUseCount(id, count.intValue());
                        }
                        if (count < 0) {
                            //扣减辅助核算类别使用计数
                            assistCalculateCateService.deductUseCount(id, -count.intValue());
                        }
                    });

                    //增加或扣减辅助核算引用计数
                    finalAssistCalculateIdCount.forEach((id, count) -> {
                        if (count > 0) {
                            //增加辅助核算使用计数
                            assistCalculateSummaryService.addUseCount(id, count.intValue());
                        }
                        if (count < 0) {
                            //扣减辅助核算使用计数
                            assistCalculateSummaryService.deductUseCount(id, -count.intValue());
                        }
                    });
                }
                //# endregion 增加或扣减相关引用计数

                //持久化mq消息
                saveVoucherMqForm.setId(voucher.getId());
                saveVoucherMqForm.setRequestId(mqMsg.getRequestId());
                saveVoucherMqForm.setFileIds(form.getFileIds());
                mqMsg.setMqKey(voucher.getId().toString());
                try {
                    mqMsg.setMsg(jsonMapper.writeValueAsString(saveVoucherMqForm));
                } catch (JsonProcessingException jsonEx) {
                    throw new BizException("序列化异常", jsonEx);
                }
                mqMsgService.create(mqMsg);
                return true;
            });

            //创建mq消息
            Message<MqMsg> msg = MessageBuilder.withPayload(mqMsg)
                    .setHeader(RocketMQHeaders.KEYS, voucher.getId()) // 设置消息 key
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
        } catch (Exception ex) {
            throw new BizException("保存凭证异常", ex);
        } finally {
            if (executor != null) {
                executor.shutdownNow();
            }
            rLock.unlock();
        }

        return true;
    }

    /**
     * 分页查询凭证
     *
     * @param form
     * @return
     */
    @Override
    public PageInfo<ListVoucherVo> list(ListVoucherForm form) {
        PageInfo<ListVoucherVo> result = new PageInfo<>();
        try {
            PageInfo<VoucherDocuemt> esData = listByES(form);
            if (CollectionUtils.isEmpty(esData.getList())) {
                return result;
            }
            esData.getList().forEach(item1 -> {
                if (CollectionUtils.isEmpty(item1.getSubjectDocuments())) {
                    throw new BizException("科目数据异常");
                }
                item1.getSubjectDocuments().forEach(item2 -> {
                    StringBuilder fullName = new StringBuilder();
                    if (!CollectionUtils.isEmpty(item2.getParentSubjectList())) {
                        for (Map.Entry<Long, String> subject : item2.getParentSubjectList().entrySet()) {
                            if (fullName.length() == 0) {
                                fullName.append(subject.getValue());
                            } else {
                                fullName.append("-").append(subject.getValue());
                            }
                        }
                    }
                    item2.setShowSubjectName(item2.getSubjectCode() + " " + fullName);
                    if (!CollectionUtils.isEmpty(item2.getAssistCalculateDocuments())) {
                        String tempShowSubjectName = item2.getShowSubjectName() + "-";
                        item2.setShowSubjectName(tempShowSubjectName);
                        item2.getAssistCalculateDocuments().forEach(assistCalculateDocument -> {
                            if (!item2.getShowSubjectName().equals(tempShowSubjectName)) {
                                item2.setShowSubjectName(item2.getShowSubjectName() + "_");
                            }
                            item2.setShowSubjectName(item2.getShowSubjectName() + assistCalculateDocument.getCode() + " " + assistCalculateDocument.getName());
                        });
                    }
                });
            });
            result = objectConvertor.toListVoucherVo(esData);
        } catch (Exception ex) {
            log.error("查询搜索引擎异常", ex);
        }
        return result;
    }

    public PageInfo<VoucherDocuemt> listByES(ListVoucherForm form) throws IOException {
        Query query = Query.of(q -> q.matchAll(m -> m));
        // 否则构建组合查询
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        boolQueryBuilder.must(m -> m.term(t -> t.field("tenantId").value(tokenService.getThreadLocalTenantId())));
        if (Strings.isNotBlank(form.getNotes())) {
            boolQueryBuilder.must(m -> m.match(t -> t.field("notes").query(form.getNotes())));
        }
        if (form.getMemberId() != null && form.getMemberId() > 0) {
            boolQueryBuilder.must(m -> m.term(t -> t.field("memberId").value(form.getMemberId())));
        }
        if (form.getBeginVoucherNumber() != null && form.getEndVoucherNumber() != null) {
            boolQueryBuilder.must(m -> m.range(t -> t.field("voucherNumber")
                    .from(form.getBeginVoucherNumber().toString())
                    .to(form.getEndVoucherNumber().toString())));
        }
        if (form.getBeginVoucherDate() != null && form.getEndVoucherDate() != null) {
            boolQueryBuilder.must(m -> m.range(t -> t.field("voucherDate")
                    .from(form.getBeginVoucherDate().toString())
                    .to(form.getEndVoucherDate().toString())));
        }
        if (form.getBeginTotalAmount() != null && form.getEndTotalAmount() != null) {
            boolQueryBuilder.must(m -> m.range(t -> t.field("totalAmount")
                    .from(form.getBeginTotalAmount().toString())
                    .to(form.getEndTotalAmount().toString())));
        }
        if (Strings.isNotBlank(form.getSummary())) {
            boolQueryBuilder.must(m -> m.nested(n ->
                    n.path("subjectDocuments").query(Query.of(q ->
                            q.match(m1 -> m1.field("subjectDocuments.summary").query(form.getSummary()))
                    ))
            ));
        }

        //按科目查询
        if (form.getSubjectId() != null) {
            boolQueryBuilder.must(m -> m.nested(n ->
                    n.path("subjectDocuments").query(QueryBuilders.term(q ->
                            q.field("subjectDocuments.subjectId").value(form.getSubjectId()))
                    ))
            );
        }
        if (boolQueryBuilder.hasClauses()) {
            query = boolQueryBuilder.build()._toQuery();
        }
        Query finalQuery = query;
        // 创建SearchRequest
        SearchRequest searchRequest = SearchRequest.of(
                builder -> {
                    builder.index("voucher");
                    builder.query(finalQuery);
                    if (form.getSortRule().equals(ListVoucherSortFieldEnum.VOUCHER_NUMBER_ASC.getCode())) {
                        builder.sort(s -> s.field(f -> f.field("voucherNumber").order(SortOrder.Asc)));
                    }
                    if (form.getSortRule().equals(ListVoucherSortFieldEnum.VOUCHER_NUMBER_DESC.getCode())) {
                        builder.sort(s -> s.field(f -> f.field("voucherNumber").order(SortOrder.Desc)));
                    }
                    if (form.getSortRule().equals(ListVoucherSortFieldEnum.VOUCHER_DATE_ASC.getCode())) {
                        builder.sort(s -> s.field(f -> f.field("voucherDate").order(SortOrder.Asc)));
                    }
                    if (form.getSortRule().equals(ListVoucherSortFieldEnum.VOUCHER_DATE_DESC.getCode())) {
                        builder.sort(s -> s.field(f -> f.field("voucherDate").order(SortOrder.Desc)));
                    }
                    builder.from(form.getOffset());
                    builder.size(form.getPageSize());
                    return builder;
                }
        );
        // 执行查询并获取结果
        SearchResponse<VoucherDocuemt> searchResponse = elasticsearchClient.search(searchRequest,
                VoucherDocuemt.class
        );
        List<VoucherDocuemt> listData = new ArrayList<>();
        // 获取结果集
        List<Hit<VoucherDocuemt>> hits = searchResponse.hits().hits();
        for (Hit<VoucherDocuemt> hit : hits) {
            VoucherDocuemt voucherDocuemt = hit.source();
            listData.add(voucherDocuemt);
        }
        PageInfo<VoucherDocuemt> pageInfo = new PageInfo<>();
        pageInfo.setTotal(Integer.parseInt(String.valueOf(searchResponse.hits().total().value())));
        pageInfo.setList(listData);
        return pageInfo;
    }

    /**
     * 查询凭证明细
     *
     * @param id
     * @return
     */
    @Override
    public GetVoucherVo get(long id) {
        MyBatisWrapper<Voucher> wrapper = new MyBatisWrapper<>();
        wrapper.select()
                .whereBuilder()
                .andEq(Id, id)
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        Voucher voucher = mapper.get(wrapper);
        GetVoucherVo result = objectConvertor.toGetVoucherVo(voucher);
        if (result == null) {
            return null;
        }
        //获取凭证id列表
        Set<Long> voucherIds = new HashSet<>();
        voucherIds.add(result.getId());

        //根据凭证id列表查询凭证科目明细
        List<GetVoucherVo.VoucherSubjectDetailVo> voucherSubjectDetailVos =
                objectConvertor.toGetVoucherSubjectDetailVo(voucherSubjectDetailService.listByVoucherIds(voucherIds));
        Set<Long> voucherWordIds = new HashSet<>();
        voucherWordIds.add(result.getVoucherWordConfigId());
        List<VoucherWordConfig> voucherWordConfigs = voucherWordConfigService.listByIds(voucherWordIds);
        if (CollectionUtils.isEmpty(voucherWordConfigs)) {
            throw new BizException("凭证字为空");
        }

        //获取科目id列表
        Set<Long> subjectIds = voucherSubjectDetailVos.stream().map(GetVoucherVo.VoucherSubjectDetailVo::getSubjectId)
                .collect(Collectors.toSet());
        List<Subject> subjects = subjectService.listIdAndCodeAndName(subjectIds);
        if (CollectionUtils.isEmpty(subjects)) {
            throw new BizException("科目不存在");
        }
        Map<Long, String> subjectFullNames = subjectService.getSubjectFullName(subjects);

        //查询科目辅助核算明细
        List<VoucherSubjectAssistCalculateDetail> voucherSubjectAssistCalculateDetailList
                = voucherSubjectAssistCalculateDetailService.listByVoucherId(id);
        List<AssistCalculateSummary> assistCalculateSummaryList = null;
        if (!CollectionUtils.isEmpty(voucherSubjectAssistCalculateDetailList)) {
            //获取辅助核算id列表
            Set<Long> assistCalculateIds = voucherSubjectAssistCalculateDetailList.stream()
                    .map(VoucherSubjectAssistCalculateDetail::getAssistCalculateId)
                    .collect(Collectors.toSet());
            assistCalculateSummaryList = assistCalculateSummaryService.list(assistCalculateIds, tokenService.getThreadLocalTenantId());
            if (CollectionUtils.isEmpty(assistCalculateSummaryList)) {
                throw new BizException("辅助核算汇总表查询异常");
            }
        }

        //获取科目的币别id列表
        Set<Long> currencyConfigIds = voucherSubjectDetailVos.stream()
                .filter(p -> !Objects.isNull(p.getCurrencyConfigId()) && p.getCurrencyConfigId() > 0)
                .map(GetVoucherVo.VoucherSubjectDetailVo::getCurrencyConfigId)
                .collect(Collectors.toSet());

        //查询币别
        List<CurrencyConfig> currencyConfigs = null;
        if (!CollectionUtils.isEmpty(currencyConfigIds)) {
            currencyConfigs = currencyConfigService.list(currencyConfigIds);
        }

        List<AssistCalculateSummary> finalAssistCalculateSummaryList = assistCalculateSummaryList;
        List<CurrencyConfig> finalCurrencyConfigs = currencyConfigs;

        //查找科目明细
        List<GetVoucherVo.VoucherSubjectDetailVo> childVoucherSubjectDetailVos =
                voucherSubjectDetailVos.stream().filter(p -> Objects.equals(result.getId(), p.getVoucherId()))
                        .collect(Collectors.toList());

        //设置科目名称
        childVoucherSubjectDetailVos.forEach((detailVo) -> {
            //设置科目编码+科目名称
            subjects.stream().filter(p -> p.getId().equals(detailVo.getSubjectId()))
                    .findFirst().ifPresent(p -> {
                detailVo.setSubjectCode(p.getCode());
                detailVo.setSubjectName(p.getName());
                if (!CollectionUtils.isEmpty(subjectFullNames) && subjectFullNames.containsKey(p.getId())) {
                    detailVo.setSubjectFullName(subjectFullNames.get(p.getId()));
                }
                detailVo.setShowSubjectName(p.getCode() + " " + detailVo.getSubjectFullName());
            });
            //  设置辅助编码+辅助名称
            // 科目辅助核算明细不能为空并且辅助核算值列表不能为空才能设置值
            if (!CollectionUtils.isEmpty(voucherSubjectAssistCalculateDetailList)
                    && !CollectionUtils.isEmpty(finalAssistCalculateSummaryList)) {
                detailVo.setAssistCalculateConfigs(new ArrayList<>());
                voucherSubjectAssistCalculateDetailList.forEach(p -> {
                    if (p.getVoucherSubjectDetailId().equals(detailVo.getId())) {
                        finalAssistCalculateSummaryList.stream()
                                .filter(i -> Objects.equals(i.getId(), p.getAssistCalculateId()))
                                .findFirst().ifPresent(i -> {

                            detailVo.setShowSubjectName(detailVo.getShowSubjectName() + "_" + i.getCode() + " " + i.getName());
                            //设置选择的辅助核算
                            GetVoucherVo.AssistCalculateConfigVo assistCalculateConfigVo = new GetVoucherVo.AssistCalculateConfigVo();
                            assistCalculateConfigVo.setSubjectId(detailVo.getSubjectId());
                            assistCalculateConfigVo.setAssistCalculateCateId(i.getAssistCalculateCateId());
                            assistCalculateConfigVo.setAssistCalculateId(i.getId());
                            assistCalculateConfigVo.setAssistCalculateName(i.getCode() + " " + i.getName());
                            detailVo.getAssistCalculateConfigs().add(assistCalculateConfigVo);
                        });
                    }
                });
            }
            //设置币别名称
            if (!CollectionUtils.isEmpty(finalCurrencyConfigs)) {
                finalCurrencyConfigs.stream().filter(p -> Objects.equals(p.getId(), detailVo.getCurrencyConfigId()))
                        .findFirst().ifPresent(p -> {
                    detailVo.setForeignCurrencyConfig(objectConvertor.toForeignCurrencyConfigVo2(Collections.singletonList(p)));

                    detailVo.setCurrencyConfigName(p.getName());
                });
            }
        });
        result.setVoucherSubjectDetailVoList(childVoucherSubjectDetailVos);
        result.setTotalAmount(result.getVoucherSubjectDetailVoList()
                .stream()
                .map(GetVoucherVo.VoucherSubjectDetailVo::getDebitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        voucherWordConfigs.stream()
                .filter(p -> p.getId().equals(result.getVoucherWordConfigId()))
                .findFirst()
                .ifPresent(voucherWordConfig -> result.setVoucherWord(voucherWordConfig.getVoucherWord()));
        return result;
    }

    /**
     * 获取凭证文档
     *
     * @param id
     * @return
     */
    @Override
    public VoucherDocuemt getVoucherDocument(long id) {
        VoucherDocuemt voucherDocuemt = null;
        MyBatisWrapper<Voucher> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, VoucherWordConfigId, VoucherNumber,
                VoucherDate, DocumentNum, TotalAmount, Notes, MemberId, TenantId)
                .whereBuilder()
                .andEq(Id, id)
                .andEq(DelFlag, false);
        Voucher voucher = mapper.get(wrapper);
        if (voucher == null) {
            throw new BizException("凭证不存在");
        }
        voucherDocuemt = objectConvertor.toVoucherES(voucher);
        VoucherWordConfig voucherWordConfig = voucherWordConfigService.getVoucherWordConfig(voucher.getVoucherWordConfigId());
        voucherDocuemt.setVoucherWord(voucherWordConfig.getVoucherWord());
        List<VoucherSubjectDetail> voucherSubjectDetailList = voucherSubjectDetailService.listByVoucherId(id);
        voucherDocuemt.setSubjectDocuments(objectConvertor.toSubjectES(voucherSubjectDetailList));
        //获取科目id列表
        Set<Long> subjectIds = voucherSubjectDetailList.stream().map(VoucherSubjectDetail::getSubjectId)
                .collect(Collectors.toSet());
        List<Subject> subjects = subjectService.listIdAndCodeAndName(subjectIds);
        if (CollectionUtils.isEmpty(subjects)) {
            throw new BizException("科目不存在");
        }

        List<VoucherSubjectAssistCalculateDetail> voucherSubjectAssistCalculateDetailList = voucherSubjectAssistCalculateDetailService.listByVoucherId(id);
        //获取辅助核算id列表
        Set<Long> assistCalculateIds = voucherSubjectAssistCalculateDetailList.stream().map(VoucherSubjectAssistCalculateDetail::getAssistCalculateId)
                .collect(Collectors.toSet());
        List<AssistCalculateSummary> assistCalculateSummaryList = assistCalculateSummaryService.list(assistCalculateIds, voucher.getTenantId());

        //获取科目的币别id列表
        Set<Long> currencyConfigIds = voucherSubjectDetailList.stream()
                .filter(p -> !Objects.isNull(p.getCurrencyConfigId()) && p.getCurrencyConfigId() > 0)
                .map(VoucherSubjectDetail::getCurrencyConfigId)
                .collect(Collectors.toSet());

        //查询币别
        List<CurrencyConfig> currencyConfigs = null;
        if (!CollectionUtils.isEmpty(currencyConfigIds)) {
            currencyConfigs = currencyConfigService.listByIds(currencyConfigIds);
        }
        List<CurrencyConfig> finalCurrencyConfigs = currencyConfigs;
        Map<Long, List<Subject>> parentSubjectList = subjectService.getParentSubjectList(subjects);

        voucherDocuemt.getSubjectDocuments().forEach(item -> {
            if (!CollectionUtils.isEmpty(assistCalculateSummaryList)) {
                List<VoucherSubjectAssistCalculateDetail> temp = voucherSubjectAssistCalculateDetailList.stream().
                        filter(p -> p.getVoucherSubjectDetailId().equals(item.getId())).collect(Collectors.toList());
                item.setAssistCalculateDocuments(objectConvertor.toAssistCalculateES(temp));

                item.getAssistCalculateDocuments().forEach(item2 -> {
                    assistCalculateSummaryList.stream().filter(p -> p.getId().equals(item2.getAssistCalculateId()))
                            .findFirst().ifPresent(p -> {
                        item2.setCode(p.getCode());
                        item2.setName(p.getName());
                    });
                });
            }

            item.setParentSubjectList(new HashMap<>());
            subjects.stream().filter(p -> p.getId().equals(item.getSubjectId())).findFirst()
                    .ifPresent(p -> {
                        item.setSubjectCode(p.getCode());
                        item.setSubjectName(p.getName());
                        List<Subject> subjectList = parentSubjectList.get(p.getId());
                        subjectList.forEach(subject -> {
                            item.getParentSubjectList().put(subject.getId(), subject.getName());
                        });
                    });

            voucherSubjectDetailList.stream().filter(p -> p.getId().equals(item.getId())).findFirst()
                    .ifPresent(p -> {
                        item.setCurrencyConfigId(p.getCurrencyConfigId());
                        item.setExchangeRate(p.getExchangeRate());
                        item.setOriginalCurrency(p.getOriginalCurrency());
                        item.setSubjectNum(p.getSubjectNum());
                        item.setSubjectUnitPrice(p.getSubjectUnitPrice());
                        item.setBalance(p.getBalance());
                        item.setDebitAmount(p.getDebitAmount());
                        item.setCreditAmount(p.getCreditAmount());
                    });

            if (!CollectionUtils.isEmpty(finalCurrencyConfigs)) {
                finalCurrencyConfigs.stream().filter(p -> p.getId().equals(item.getCurrencyConfigId())).findFirst()
                        .ifPresent(p -> {
                            item.setCurrencyConfigName(p.getName());
                        });
            }
        });
        return voucherDocuemt;
    }

    /**
     * 获取凭证相关信息
     *
     * @param id
     * @return
     */
    @Override
    public Voucher getByIdAndTenantId(long id) {
        MyBatisWrapper<Voucher> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, VoucherWordConfigId, Version)
                .whereBuilder()
                .andEq(Id, id)
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        return mapper.get(wrapper);
    }

    /**
     * 获取凭证相关信息
     *
     * @param id
     * @return
     */
    @Override
    public Voucher getById(long id) {
        MyBatisWrapper<Voucher> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, VoucherWordConfigId, Version, VoucherNumber, TenantId)
                .whereBuilder()
                .andEq(Id, id)
                .andEq(DelFlag, false);
        return mapper.get(wrapper);
    }

    /**
     * 保存凭证到es
     *
     * @param mqMsg
     */
    @Override
    public void saveVoucherToEs(SaveVoucherMqForm form, MqMsg mqMsg) {
        long voucherId = Long.parseLong(mqMsg.getMqKey());
        RLock rLock = redissonClient.getLock(RedisKeyConstant.SAVE_VOUCHER_LOCK + mqMsg.getDataNo());
        try {
            rLock.lock();
            VoucherDocuemt voucherES = getVoucherDocument(voucherId);
            if (mqMsg.getMsgStatus().equals(MqMsgStatusEnum.SUCCESS.getCode())
                    || mqMsg.getMsgStatus().equals(MqMsgStatusEnum.FAIL.getCode())) {
                log.info("消息：{}已被消费", mqMsg.getRequestId());
                return;
            }
            //保存文件映射
            fileRefMappingService.save(FileRefTypeEnum.VOUCHER, form.getId(), form.getFileIds(),
                    voucherES.getTenantId(), voucherES.getMemberId());
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index("voucher")
                    .id(mqMsg.getMqKey())
                    .document(voucherES)
            );
            mqMsgService.success(mqMsg);
            log.info("创建凭证文档返回结果：{}", response.toString());
        } catch (Exception e) {
            mqMsg.setErrorMsg(e.getMessage());
            mqMsgService.fail(mqMsg);
            throw new BizException("消费失败");
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 从es中删除凭证
     *
     * @param mqMsg
     */
    @Override
    public void delVoucherToEs(MqMsg mqMsg) {
        RLock rLock = redissonClient.getLock(RedisKeyConstant.SAVE_VOUCHER_LOCK + mqMsg.getDataNo());
        try {
            rLock.lock();
            MqMsg msg = mqMsgService.get(mqMsg.getId());
            if (msg == null) {
                throw new BizException("消息不存在");
            }
            if (msg.getMsgStatus().equals(MqMsgStatusEnum.SUCCESS.getCode())
                    || msg.getMsgStatus().equals(MqMsgStatusEnum.FAIL.getCode())) {
                log.info("消息：{}已被消费", msg.getRequestId());
                return;
            }
            DeleteRequest deleteRequest = new DeleteRequest.Builder()
                    .id(mqMsg.getMqKey())
                    .index("voucher")
                    .build();
            DeleteResponse deleteResponse = elasticsearchClient.delete(deleteRequest);

            mqMsgService.success(mqMsg);
            log.info("删除凭证文档返回结果：{}", deleteResponse.toString());
        } catch (Exception e) {
            mqMsg.setErrorMsg(e.getMessage());
            mqMsgService.fail(mqMsg);
            throw new BizException("消费失败");
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 处理定时任务消息
     *
     * @param form
     */
    @EventListener
    @Override
    public void handleMqMsg(SaveVoucherMqForm form) {
        RLock rLock = redissonClient.getLock(RedisKeyConstant.SAVE_VOUCHER_LOCK + form.getRequestId());
        try {
            rLock.lock();
            MqMsg mqMsg = mqMsgService.get(form.getRequestId());
            saveVoucherToEs(form,mqMsg);
        } catch (Exception ex) {
            throw ex;
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 处理定时任务消息
     *
     * @param form
     */
    @EventListener
    @Override
    public void handleMqMsg(DelVoucherMqForm form) {
        MqMsg mqMsg = mqMsgService.get(form.getRequestId());
        delVoucherToEs(mqMsg);
    }

    /**
     * 将凭证数据刷新至es中
     */
    @Override
    public void resetVoucherDocument() throws IOException {
        RLock rLock1 = redissonClient.getLock(RedisKeyConstant.SAVE_VOUCHER_LOCK + "0");
        // 创建 ThreadPoolExecutor
        ThreadPoolExecutor executor = null;
        // 获取系统的CPU核心数
        int cpuCores = Runtime.getRuntime().availableProcessors();
        try {
            rLock1.lock();
            executor = new ThreadPoolExecutor(
                    cpuCores,  // 核心线程池大小
                    cpuCores * 2,  // 最大线程池大小
                    60, // 线程空闲时间
                    TimeUnit.SECONDS, // 时间单位
                    new LinkedBlockingQueue<>(100)); // 任务队列
            String indexName = "voucher";
            // 检查索引是否存在
            co.elastic.clients.elasticsearch.indices.ExistsRequest existsRequest = new ExistsRequest.Builder()
                    .index(indexName)
                    .build();
            BooleanResponse existsResponse = elasticsearchClient.indices().exists(existsRequest);
            //如果索引存在则删除索引
            if (existsResponse.value()) {
                DeleteIndexRequest request = new DeleteIndexRequest.Builder().index(indexName)
                        .build();
                elasticsearchClient.indices().delete(request);
            }

            // 创建索引请求
            CreateIndexRequest createIndexRequest = CreateIndexRequest.of(builder -> builder
                            .index(indexName)
                            .mappings(m -> m
                                    .properties("subjectDocuments", Property.of(builder1 ->
                                            builder1.nested(n -> n.enabled(true))))
                                    .properties("voucherDate", Property.of(builder1 ->
                                            builder1.date(n -> n.format("yyyy-MM-dd HH:mm:ss"))))
                            )
                    // 可以设置其他参数，如分片数、副本数等
            );

            elasticsearchClient.indices().create(createIndexRequest);
            long minId = 0;
            List<Voucher> vouchers = null;
            while (!CollectionUtils.isEmpty(vouchers = list(minId, 100))) {
                CountDownLatch latch = new CountDownLatch(vouchers.size());
                for (Voucher voucher : vouchers) {
                    executor.submit(() -> {
                        RLock rLock = redissonClient.getLock(RedisKeyConstant.SAVE_VOUCHER_LOCK + voucher.getTenantId() + ":" + voucher.getVoucherNumber());
                        try {
                            rLock.lock();
                            log.info("正在保存凭证到es：{}", voucher.getId());
                            saveVoucherToEs(voucher.getId());
                        } catch (Exception ex) {
                            log.error("保存凭证到es异常", ex);
                        } finally {
                            rLock.unlock();
                            latch.countDown(); // 任务执行完成时减少计数器
                        }
                    });
                    minId = voucher.getId();
                }
                // 等待计数器归零
                latch.await();
            }
        } catch (InterruptedException ex) {
            log.error("保存凭证到es异常", ex);
        } finally {
            if (executor != null) {
                executor.shutdownNow();
            }
            rLock1.unlock();
        }
    }

    /**
     * 删除凭证
     *
     * @param form
     * @return
     */
    @Override
    public boolean del(DelVoucherForm form) {
        RLock rLock = redissonClient.getLock(RedisKeyConstant.SAVE_VOUCHER_LOCK + form.getVoucherNumber());
        try {
            rLock.lock();
            GetVoucherVo getVoucherVo = get(form.getId());
            if (getVoucherVo == null || !Objects.equals(getVoucherVo.getVoucherNumber(), form.getVoucherNumber())) {
                throw new BizException("凭证不存在");
            }

            Map<Long, Long> subjectIdCount = getVoucherVo.getVoucherSubjectDetailVoList().stream()
                    .collect(Collectors.groupingBy(GetVoucherVo.VoucherSubjectDetailVo::getSubjectId,
                            Collectors.counting()));

            //币别计数
            Map<Long, Long> currencyConfigIdCount = getVoucherVo.getVoucherSubjectDetailVoList().stream()
                    .filter(p -> p.getCurrencyConfigId() != null && p.getCurrencyConfigId() > 0)
                    .collect(Collectors.groupingBy(GetVoucherVo.VoucherSubjectDetailVo::getCurrencyConfigId,
                            Collectors.counting()));

            //辅助核算类别计数
            Map<Long, Long> assistCalculateCateIdCount = getVoucherVo.getVoucherSubjectDetailVoList().stream()
                    .flatMap(voucherSubjectDetailVo -> voucherSubjectDetailVo.getAssistCalculateConfigs().stream())
                    .collect(Collectors.groupingBy(GetVoucherVo.AssistCalculateConfigVo::getAssistCalculateCateId,
                            Collectors.counting()));
            //辅助核算计数
            Map<Long, Long> assistCalculateIdCount = getVoucherVo.getVoucherSubjectDetailVoList().stream()
                    .flatMap(voucherSubjectDetailVo -> voucherSubjectDetailVo.getAssistCalculateConfigs().stream())
                    .collect(Collectors.groupingBy(GetVoucherVo.AssistCalculateConfigVo::getAssistCalculateId,
                            Collectors.counting()));

            MqMsg mqMsg = new MqMsg();
            mqMsg.setMqTopic(MqConstant.TOPIC_VOUCHER);
            mqMsg.setMqTag(MqConstant.TAG_DEL_VOUCHER);
            mqMsg.setRequestId(UUID.randomUUID().toString());
            mqMsg.setMsgClassName(DelVoucherMqForm.class.getName());
            mqMsg.setDataNo(getVoucherVo.getVoucherNumber().toString());
            DelVoucherMqForm delVoucherMqForm = new DelVoucherMqForm();
            transactionTemplate.execute((transactionStatus) -> {
                //更新凭证
                del(form.getId());
                voucherSubjectDetailService.del(form.getId());
                voucherSubjectAssistCalculateDetailService.del(form.getId());
                //解除凭证字的引用计数
                voucherWordConfigService.deductUseCount(getVoucherVo.getVoucherWordConfigId(), 1);

                subjectIdCount.forEach((id, count) -> {
                    //扣减科目引用计数
                    subjectService.deductUseCount(id, count.intValue());
                });

                currencyConfigIdCount.forEach((id, count) -> {
                    //扣减币别引用计数
                    currencyConfigService.decrUseCount(id, count.intValue());
                });

                assistCalculateCateIdCount.forEach((id, count) -> {
                    //扣减辅助核算类别使用计数
                    assistCalculateCateService.deductUseCount(id, count.intValue());
                });

                assistCalculateIdCount.forEach((id, count) -> {
                    //扣减辅助核算使用计数
                    assistCalculateSummaryService.deductUseCount(id, count.intValue());
                });

                delVoucherMqForm.setId(form.getId());
                delVoucherMqForm.setRequestId(mqMsg.getRequestId());
                mqMsg.setMqKey(form.getId().toString());
                try {
                    mqMsg.setMsg(jsonMapper.writeValueAsString(delVoucherMqForm));
                } catch (JsonProcessingException jsonEx) {
                    throw new BizException("序列化异常", jsonEx);
                }
                mqMsgService.create(mqMsg);
                return true;
            });
            Message<MqMsg> msg = MessageBuilder.withPayload(mqMsg)
                    .setHeader(RocketMQHeaders.KEYS, form.getId()) // 设置消息 key
                    .build();
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
        } catch (Exception ex) {
            throw ex;
        } finally {
            rLock.unlock();
        }

        return true;
    }

    private List<Voucher> list(long minId, int top) {
        MyBatisWrapper<Voucher> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, VoucherNumber, TenantId)
                .whereBuilder()
                .andGT(Id, minId)
                .andEq(DelFlag, false);
        wrapper.limit(top);
        wrapper.orderByAsc(Id);
        return mapper.list(wrapper);
    }

    private void saveVoucherToEs(long voucherId) {
        VoucherDocuemt voucherES = getVoucherDocument(voucherId);
        try {
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index("voucher")
                    .id(String.valueOf(voucherId))
                    .document(voucherES)
            );
            log.info("创建凭证文档返回结果：{}", response.toString());
        } catch (Exception e) {
            throw new BizException("消费失败");
        }
    }


    /**
     * 更新凭证
     *
     * @param form
     * @param version
     * @return
     */
    private boolean update(CreateVoucherForm form, int version) {
        MyBatisWrapper<Voucher> wrapper = new MyBatisWrapper<>();
        wrapper.update(VoucherWordConfigId, form.getVoucherWordConfigId())
                .update(VoucherNumber, form.getVoucherNumber())
                .update(VoucherDate, form.getVoucherDate())
                .update(DocumentNum, form.getDocumentNum())
                .update(TotalAmount, form.getTotalAmount())
                .update(Notes, form.getNotes())
                .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                .update(UpdateTime, DateUtil.getSystemTime())
                .updateIncr(Version, 1)

                .whereBuilder()
                .andEq(Id, form.getId())
                .andEq(DelFlag, false)
                .andEq(Version, version)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("修改凭证失败");
        }
        return true;
    }

    private boolean del(long id) {
        MyBatisWrapper<Voucher> wrapper = new MyBatisWrapper<>();
        wrapper.update(DelFlag, true)
                .update(UpdateMemberId, tokenService.getThreadLocalUserId())
                .update(UpdateTime, DateUtil.getSystemTime())
                .updateIncr(Version, 1)

                .whereBuilder()
                .andEq(Id, id)
                .andEq(DelFlag, false)
                .andEq(TenantId, tokenService.getThreadLocalTenantId());
        if (mapper.updateField(wrapper) == 0) {
            throw new BizException("删除凭证失败");
        }
        return true;
    }

    /**
     * 判断凭证编号是否存在
     *
     * @param voucherNumber
     * @return
     */
    private boolean isExistByVoucherNumber(Long id, int voucherNumber, long tenantId) {
        MyBatisWrapper<Voucher> wrapper = new MyBatisWrapper<>();
        wrapper.select(Id, VoucherNumber, Version)
                .whereBuilder()
                .andEq(VoucherNumber, voucherNumber)
                .andEq(DelFlag, false)
                .andEq(TenantId, tenantId);
        Voucher voucher = mapper.get(wrapper);
        if (voucher == null) {
            return false;
        }
        if (id == null || id == 0) {
            return true;
        }
        if (id.compareTo(voucher.getId()) != 0) {
            return true;
        }
        return false;
    }
}
