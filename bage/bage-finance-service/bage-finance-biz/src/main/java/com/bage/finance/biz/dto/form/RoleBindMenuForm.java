package com.bage.finance.biz.dto.form;


import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;

import java.util.List;

@Data
public class RoleBindMenuForm {

    /**
     * 角色id
     */
    @NotNull
    @Range(min = 1, max = Long.MAX_VALUE)
    private Integer roleId;

    /**
     * 绑定菜单id列表
     */
    private List<Integer> bindMenuIds;

    /**
     * 解绑菜单id列表
     */
    private List<Integer> unBindMenuIds;
}