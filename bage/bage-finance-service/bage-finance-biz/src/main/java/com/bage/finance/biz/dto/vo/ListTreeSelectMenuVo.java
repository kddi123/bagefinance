package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.util.List;

@Data
public class ListTreeSelectMenuVo {
    /**
     * id
     */
    private Integer id;
    /**
     * 值
     */
    private String value;
    /**
     * 菜单名称
     */
    private String title;
    /**
     * 子菜单
     */
    private List<ListTreeSelectMenuVo> children;
}
