package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 辅助核算
 */
@Data
public class UpdateAssistCalculateForm {
    /**
     * id
     */
    @NotNull
    private Long id;

    /**
     * 编码
     * 客户编码/供应商编码/职员编码/部门编码/项目编码/存货编码/现金流编码/编码
     */
    @NotBlank
    private String code;

    /**
     * 辅助核算类别名称
     * 客户名称/供应商名称/职员名称/部门名称/项目名称/存货名称/现金流名称/名称
     */
    @NotBlank
    private String name;

    /**
     * 备注
     */
    private String notes;

    /**
     * 助记码
     */
    private String mnemonicCode;

}