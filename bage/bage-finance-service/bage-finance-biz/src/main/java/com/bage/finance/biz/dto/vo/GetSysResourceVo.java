package com.bage.finance.biz.dto.vo;


import lombok.Data;

/**
 * 获取资源
 */
@Data
public class GetSysResourceVo {
    /**
     * 资源id
     */
    private Integer id;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 上级pid
     */
    private Integer pid;

    /**
     * 上级资源名称
     */
    private String parentName;

    /**
     * 资源路径
     */
    private String path;
}
