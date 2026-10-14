package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


@Data
public class PhonePasswordLoginForm {

    /**
     * 客户端id
     */
    @NotBlank(message = "请输入客户端id")
    @Pattern(regexp = "^[0-9A-Za-z]{6,32}$", message = "clientId非法")
    private String clientId;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式错误！")
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "请输入密码")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\\\W]{6,18}$", message = "密码长度需在6~18位字符，且必须包含字母和数字！")
    private String password;

    /**
     * 图形验证码
     */
    @NotBlank(message = "请输入图形验证码")
    @Pattern(regexp = "^[a-zA-Z0-9]{5}$", message = "图形验证码格式不正确")
    private String code;
}
