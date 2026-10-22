package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class UpdatePhoneForm {
    /**
     * 手机号
     */
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式错误！")
    @NotBlank
    private String phone;

    /**
     * 短信验证码
     */
    @Pattern(regexp = "^[0-9]{4,6}$", message = "短信验证码格式不正确")
    @NotBlank
    private String smsCode;
}
