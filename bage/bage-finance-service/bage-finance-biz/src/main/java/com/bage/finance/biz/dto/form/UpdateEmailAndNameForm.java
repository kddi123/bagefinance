package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class UpdateEmailAndNameForm {
    /**
     * 邮件地址
     */
    @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$", message = "邮箱格式不正确")
    @NotBlank
    private String email;

    /**
     * 姓名
     */
    @NotBlank
    @Size(max = 50)
    private String name;

}
