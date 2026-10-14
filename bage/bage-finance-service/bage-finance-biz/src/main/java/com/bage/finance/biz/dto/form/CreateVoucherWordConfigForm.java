package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 创建凭证字
 */
@Data
public class CreateVoucherWordConfigForm {

    /**
     * 凭证字
     */
    @NotNull
    @Size(min = 1, max = 10)
    private String voucherWord;

    /**
     * 打印标题
     */
    @NotBlank
    @Size(min = 1, max = 50)
    private String printTitle;
}