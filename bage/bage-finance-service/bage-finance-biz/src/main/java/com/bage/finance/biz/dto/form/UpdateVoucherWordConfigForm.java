package com.bage.finance.biz.dto.form;


import lombok.Data;
import org.hibernate.validator.constraints.Range;

 import jakarta.validation.constraints.NotBlank;
 import jakarta.validation.constraints.NotNull;
 import jakarta.validation.constraints.Size;

/**
 * 修改凭证字
 *
 */
@Data
public class UpdateVoucherWordConfigForm {
    /**
     *凭证字Id
     */
    @NotNull
    @Range(min = 1)
    private Long id;

    /**
     *凭证字
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