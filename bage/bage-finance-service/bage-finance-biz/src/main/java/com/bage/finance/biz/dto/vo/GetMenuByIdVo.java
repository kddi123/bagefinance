package com.bage.finance.biz.dto.vo;


import lombok.Data;

@Data
public class GetMenuByIdVo {
    /**
     * id
     */
    private Integer id;

    /**
     * 父级菜单id
     */
    private Integer pid;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单路由
     */
    private String path;

    /**
     * 菜单组件
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否使用布局
     */
    private Boolean layout;

    /**
     * 是否隐藏菜单
     */
    private Boolean hideInMenu;

    /**
     * 重定向地址
     */
    private String redirect;

    /**
     * 访问权限
     */
    private String access;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否禁用
     */
    private Boolean disable;
}