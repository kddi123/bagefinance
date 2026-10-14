package com.bage.finance.biz.service;

import com.bage.finance.biz.domain.MqMsg;

public interface MqMsgService {
    /**
     * 保存mq消息
     *
     * @param mqMsg
     * @return
     */
    boolean create(MqMsg mqMsg);

    /**
     * 消费成功
     *
     * @param mqMsg
     * @return
     */
    boolean success(MqMsg mqMsg);

    /**
     * 消费失败
     *
     * @param mqMsg
     * @return
     */
    boolean fail(MqMsg mqMsg);

    /**
     * 获取一条mq消息
     *
     * @param id
     * @return
     */
    MqMsg get(long id);

    /**
     * 获取一条mq消息
     *
     * @param requestId
     * @return
     */
    MqMsg get(String requestId);

    /**
     * 通过定时任务处理消息
     */
    void handleMqMessage();

    /**
     * 消费失败
     *
     * @param mqMsg
     * @return
     */
    boolean failByRequestId(MqMsg mqMsg);

    /**
     * 消费成功
     *
     * @param requestId
     * @return
     */
    boolean success(String requestId);
}
