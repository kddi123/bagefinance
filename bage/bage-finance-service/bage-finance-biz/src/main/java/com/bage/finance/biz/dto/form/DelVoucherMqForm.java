package com.bage.finance.biz.dto.form;

import lombok.Data;

@Data
public class DelVoucherMqForm {
    /**
     * 凭证id
     */
    private Long id;

    /**
     * 消息请求id
     */
    private String requestId;
}
