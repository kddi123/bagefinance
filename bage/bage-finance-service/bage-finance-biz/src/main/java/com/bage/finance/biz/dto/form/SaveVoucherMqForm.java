package com.bage.finance.biz.dto.form;

import lombok.Data;

import java.util.Set;

@Data
public class SaveVoucherMqForm {
    /**
     * 凭证id
     */
    private Long id;

    /**
     * 消息请求id
     */
    private String requestId;

    /**
     * 文件id列表
     */
    private Set<Long> fileIds;
}
