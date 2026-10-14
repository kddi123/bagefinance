package com.bage.finance.biz.dto.form;

import lombok.Data;

 import jakarta.validation.constraints.NotNull;

/**
 * 删除凭证字
 *
 */
@Data
public class DelVoucherWordConfigForm {

    /**
     * 凭证字id
     */
    @NotNull
    private Long id;
}