package com.bage.finance.biz.dto.form;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CreateFolderForm {

    /**
     * 上级id
     */
    @NotNull
    @Min(value = 0)
    private Long pid;

    /**
     * 文件夹名称
     */
    @NotBlank
    @Length(min = 1, max = 50)
    private String name;

    /**
     * 顺序
     */
    @NotNull
    private Integer sort;
}
