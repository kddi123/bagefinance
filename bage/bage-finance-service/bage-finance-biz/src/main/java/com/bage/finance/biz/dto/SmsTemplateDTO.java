package com.bage.finance.biz.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.List;

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
}