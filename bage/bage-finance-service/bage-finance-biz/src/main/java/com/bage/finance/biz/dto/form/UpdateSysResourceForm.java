package com.bage.finance.biz.dto.form;


import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.*;

/**
 * 修改资源
 */
@Data
public class UpdateSysResourceForm {
    /**
     * 资源id
     */
    @NotNull
    @Range(min = 1, max = Integer.MAX_VALUE)
    private Integer id;

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
