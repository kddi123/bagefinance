package com.bage.common.dto;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Data
public class PageHelperRequest implements Serializable {
    private static final long serialVersionUID = -1604684240674386686L;

    /**
     * 当前页, 默认1
     */
    @NotNull(message = "当前页不能为空")
    @Min(value = 1)
    private Integer pageNum = 1;

    /**
     * 每页记录数, 默认10
     */
    @NotNull(message = "每页记录数不能为空")
    private Integer pageSize = 10;

    /**
     * 获取对应数据库的第几行记录
     *
     * @return
     */
    public Integer getOffset() {
        return this.pageNum > 0 ? ((this.pageNum - 1) * this.pageSize) : 0;
    }
}
