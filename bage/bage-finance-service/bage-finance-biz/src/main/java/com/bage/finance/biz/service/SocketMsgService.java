package com.bage.finance.biz.service;

import com.bage.common.sofabolt.SofaboltMsgRequest;

public interface SocketMsgService<T extends SofaboltMsgRequest> {
    /**
     * 发送sofabolt消息
     * @param request
     */
    void sendMsgByClientId(T request);
}
