package com.bage.finance.biz.dto.form;


import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 修改科目
 */
@Data
public class UpdateSubjectForm {

    /**
     * 科目id
     */
    @NotNull
    private Long id;

    /**
     * 科目编码
     */
    @NotBlank
    @Size(min = 4, max = 16)
    @Pattern(regexp = "\\d+")
    private String code;

    /**
     * 科目名称
     */
    @NotBlank
    private String name;

    /**
     * 助记码
     */
    @NotNull
    private String mnemonicCode;

    /**
     * 余额方向[1：借，2：贷]
     */
    @NotNull
    private Integer balanceDirection;

    /**
     * 核算配置[数量核算，辅助核算，外币核算]
     */
    @NotNull
    private SubjectCalculateConfigForm subjectCalculateConfigForm;
}