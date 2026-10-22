package com.bage.finance.biz.dto;

import lombok.Data;

@Data
public class SmsTemplateDTO {
    /**
     * 短信签名
     */
    private String signName;

    /**
     * 短信模板编号
     */
    private String templateCode;

    /**
     * 验证码有效时长（分钟），阿里云号码认证服务模板参数 min 使用；
     * 未配置时后端默认 15 分钟
     */
    private String min;
}