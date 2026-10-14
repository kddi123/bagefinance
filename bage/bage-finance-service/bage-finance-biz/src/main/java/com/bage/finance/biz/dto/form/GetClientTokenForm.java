package com.bage.finance.biz.dto.form;


import lombok.Data;

 import jakarta.validation.constraints.NotBlank;
 import jakarta.validation.constraints.Pattern;

@Data
public class GetClientTokenForm {

    /**
     * 客户端id
     */
    @NotBlank(message = "客户端id不能为空")
    @Pattern(regexp = "^[0-9A-Za-z]{6,32}$", message = "clientId非法")
    private String clientId;
}
