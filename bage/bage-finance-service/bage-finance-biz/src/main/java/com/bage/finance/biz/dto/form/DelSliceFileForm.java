package com.bage.finance.biz.dto.form;

import lombok.Data;

@Data
public class DelSliceFileForm {
    /**
     * 会员id
     */
    private Long memberId;
    /**
     * 分片文件uid
     */
    private String uid;
    /**
     * 分片数量
     */
    private Integer totalChunks;

    /**
     * mq消息id
     */
    private String msgRequestId;
}
