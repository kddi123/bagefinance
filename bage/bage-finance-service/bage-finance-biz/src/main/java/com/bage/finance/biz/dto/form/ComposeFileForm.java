package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class ComposeFileForm {
    /**
     * 文件夹id
     */
    @NotNull
    @Min(value = 0)
    private Long folderId;

    /**
     * 总分片大小
     */
    @NotNull
    private Integer totalChunks;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件唯一编号
     */
    @Pattern(regexp = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")
    private String uid;
}
