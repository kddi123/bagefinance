package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.util.List;

@Data
public class ListTreeMenuVo {
    /**
     * id
     */
    private Integer id;
    /**
     * 主键
     */
    private String key;
    /**
     * 菜单名称
     */
    private String title;
    /**
     * icon
     */
    private String icon;
    /**
     * 默认是否选中
     */
    private Boolean checked;
    /**
     * 父节点id
     */
    private Integer pid;
    /**
     * 菜单路由
     */
    private String path;
    /**
     * 顺序
     */
    private Integer sort;
    /**
     * 是否隐藏菜单
     */
    private Boolean hideInMenu;
    /**
     * 子菜单
     */
    private List<ListTreeMenuVo> children;
}
