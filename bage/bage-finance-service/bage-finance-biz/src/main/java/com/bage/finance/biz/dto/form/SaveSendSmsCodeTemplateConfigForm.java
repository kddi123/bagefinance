package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class SaveSendSmsCodeTemplateConfigForm {

    /**
     * 配置key
     */
    @NotBlank
    @Pattern(regexp = "^REG|LOGIN|UPDATE_PHONE$", message = "短信验证码类型非法")
    private String configKey;

    /**
     * 配置名称
     */
    @NotBlank
    private String configName;

    /**
     * 短信签名
     */
    @NotBlank
    private String signName;

    /**
     * 短信模板编号
     */
    @NotBlank
    private String templateCode;
}