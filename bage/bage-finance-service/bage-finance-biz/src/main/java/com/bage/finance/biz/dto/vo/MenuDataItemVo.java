package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.util.List;

@Data
public class MenuDataItemVo {
    /**
     * 菜单id
     */
    private Integer id;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 菜单路由
     */
    private String path;

    /**
     * 是否使用布局
     */
    private Boolean layout;

    /**
     * 菜单组件
     */
    private String component;

    /**
     * 菜单重定向地址
     */
    private String redirect;

    /**
     * 是否隐藏菜单
     */
    private Boolean hideInMenu;

    /**
     * 子菜单
     */
    private List<MenuDataItemVo> routes;
}