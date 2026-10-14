package com.bage.finance.biz.dto.vo;


import lombok.Data;

@Data
public class ListRoleVo {
    /**
     * 角色id
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 是否启用禁用，true禁用，false启用
     */
    private Boolean disable;
}
