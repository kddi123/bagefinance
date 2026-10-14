package com.bage.finance.biz.dto.form;

import com.bage.common.dto.PageHelperRequest;

import lombok.Data;
import jakarta.validation.constraints.Min;

import java.util.Date;

@Data
public class ListFileForm extends PageHelperRequest {
    /**
     * 文件夹id
     */
    @Min(value = 0)
    private Long folderId;

    /**
     * 上传开始日期
     */
    private Date beginDate;

    /**
     * 上传结束日期
     */
    private Date endDate;

    /**
     * 文件名称
     */
    private String name;
}
