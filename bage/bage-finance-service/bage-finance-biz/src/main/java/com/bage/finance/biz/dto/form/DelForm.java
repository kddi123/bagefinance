package com.bage.finance.biz.dto.form;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class DelForm {

    /**
     * id
     */
    @NotNull
    private Long id;
}