package com.bage.finance.biz.dto.vo;


import lombok.Data;

@Data
public class GetFolderVo {
    /**
     * id
     */
    private Long id;

    /**
     * pid
     */
    private Long pid;

    /**
     * 上级文件夹名称
     */
    private String parentName;

    /**
     * 文件夹名称
     */
    private String name;

    /**
     * 顺序
     */
    private Integer sort;
}
