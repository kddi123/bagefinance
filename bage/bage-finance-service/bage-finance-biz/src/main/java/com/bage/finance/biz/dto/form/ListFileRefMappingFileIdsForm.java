package com.bage.finance.biz.dto.form;

import lombok.Data;

 import jakarta.validation.constraints.Min;
 import jakarta.validation.constraints.NotNull;

@Data
public class ListFileRefMappingFileIdsForm {
    /**
     * 文件引用类型
     */
    @NotNull
    @Min(value = 0)
    private Integer fileRefType;
    /**
     * 文件引用id
     */
    @NotNull
    @Min(value = 1)
    private Long fileRefId;
}