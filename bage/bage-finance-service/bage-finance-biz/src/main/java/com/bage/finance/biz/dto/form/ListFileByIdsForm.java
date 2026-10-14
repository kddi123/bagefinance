package com.bage.finance.biz.dto.form;


import lombok.Data;

import jakarta.validation.constraints.Size;

import java.util.List;

@Data
public class ListFileByIdsForm {

    /**
     * 文件id
     */
    @Size(min = 1)
    private List<Long> ids;
}