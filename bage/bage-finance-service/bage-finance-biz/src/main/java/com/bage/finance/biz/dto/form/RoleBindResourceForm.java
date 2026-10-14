package com.bage.finance.biz.dto.form;


import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;

import java.util.List;

@Data
public class RoleBindResourceForm {

    /**
     * 角色id
     */
    @NotNull
    @Range(min = 1, max = Long.MAX_VALUE)
    private Integer roleId;

    /**
     * 绑定资源id列表
     */
    private List<Integer> bindResourceIds;

    /**
     * 解绑资源id列表
     */
    private List<Integer> unBindResourceIds;
}
