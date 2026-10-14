package com.bage.finance.biz.dto.form;


import lombok.Data;

import java.util.List;

/**
 * 获取资源
 */
@Data
public class ListSysResourceForm {

    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源路径
     */
    private String path;
}
