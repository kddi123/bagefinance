package com.bage.finance.biz.service;

import com.bage.finance.biz.domain.MqMsg;
import com.bage.finance.biz.domain.Voucher;
import com.bage.finance.biz.domain.es.VoucherDocuemt;
import com.bage.finance.biz.dto.form.*;
import com.bage.finance.biz.dto.vo.GetVoucherVo;
import com.bage.finance.biz.dto.vo.ListVoucherVo;
import com.bage.mybatis.help.PageInfo;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;

public interface VoucherService {
    /**
     * 创建凭证
     *
     * @param form
     * @return
     */
    boolean save(CreateVoucherForm form);

    /**
     * 分页查询凭证
     *
     * @param form
     * @return
     */
    PageInfo<ListVoucherVo> list(ListVoucherForm form);

    /**
     * 获取凭证明细
     *
     * @param id
     * @return
     */
    GetVoucherVo get(long id);

    /**
     * 获取凭证文档
     * @param id
     * @return
     */
    VoucherDocuemt getVoucherDocument(long id);

    /**
     * 获取凭证
     * @param id
     * @return
     */
    Voucher getByIdAndTenantId(long id);

    /**
     * 获取凭证相关信息
     *
     * @param id
     * @return
     */
    Voucher getById(long id);

    /**
     * 保存凭证到es
     * @param mqMsg
     */
    void saveVoucherToEs(SaveVoucherMqForm form, MqMsg mqMsg);

    /**
     * 保存凭证到es
     *
     * @param mqMsg
     */
    void delVoucherToEs(MqMsg mqMsg);

    /**
     * 处理定时任务消息
     * @param form
     */
    @Async
    @EventListener
    void handleMqMsg(SaveVoucherMqForm form);

    /**
     * 处理定时任务消息
     *
     * @param form
     */
    void handleMqMsg(DelVoucherMqForm form);

    /**
     * 将凭证数据刷新至es中
     */
    void resetVoucherDocument() throws IOException;

    /**
     * 删除凭证
     * @param form
     * @return
     */
    boolean del(DelVoucherForm form);
}
