package com.bage.finance.biz.dto.vo;


import lombok.Data;

@Data
public class ListFolderVo {
    /**
     * id
     */
    private Long id;

    /**
     * pid
     */
    private Long pid;

    /**
     * 文件夹名称
     */
    private String name;

    /**
     * 子节点数量
     */
    private Long childCount;

    /**
     * 顺序
     */
    private Integer sort;
}
