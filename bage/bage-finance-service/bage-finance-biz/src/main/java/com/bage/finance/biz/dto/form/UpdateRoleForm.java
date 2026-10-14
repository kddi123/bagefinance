package com.bage.finance.biz.dto.form;


import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class UpdateRoleForm {
    /**
     * 角色id
     */
    @NotNull
    @Range(min = 1)
    private Integer id;

    /**
     * 角色名称
     */
    @NotBlank
    private String roleName;
}
