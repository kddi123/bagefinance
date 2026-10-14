package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Data
public class UpdateMenuForm {
    /**
     * id
     */
    @NotNull
    @Min(value = 1)
    private Integer id;

    /**
     * 菜单名称
     */
    @NotBlank
    @Size(max = 200)
    private String name;

    /**
     * 菜单路由
     */
    @NotBlank
    @Size(max = 200)
    private String path;

    /**
     * 菜单组件
     */
    @NotNull
    @Size(max = 200)
    private String component;

    /**
     * 图标
     */
    @Size(max = 20)
    private String icon;

    /**
     * 是否使用布局
     */
    @NotNull
    private Boolean layout;

    /**
     * 是否隐藏菜单
     */
    @NotNull
    private Boolean hideInMenu;

    /**
     * 重定向地址
     */
    private String redirect;

    /**
     * 排序
     */
    @NotNull
    private Integer sort;
}