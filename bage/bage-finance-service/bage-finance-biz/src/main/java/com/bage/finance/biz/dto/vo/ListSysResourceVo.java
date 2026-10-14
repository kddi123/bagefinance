package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.util.List;

/**
 * 获取资源
 */
@Data
public class ListSysResourceVo {
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
     * 资源路径
     */
    private String path;

    /**
     * 下级资源
     */
    private List<ListSysResourceVo> children;
}
