package com.bage.finance.biz.dto.form;


import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 创建资源
 */
@Data
public class CreateSysResourceForm {

    /**
     * 上级资源id
     */
    @NotNull
    @Range(min = 0, max = Integer.MAX_VALUE)
    private Integer pid;

    /**
     * 资源名称
     */
    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    /**
     * 资源路径
     */
    @NotBlank
    @Size(min = 1, max = 50)
    private String path;
}
