package com.bage.finance.biz.dto.vo;


import lombok.Data;

/**
 * 凭证字列表
 */
@Data
public class ListVoucherWordConfigVo {
    /**
     * id
     */
    private Integer id;

    /**
     * 凭证纸字
     */
    private String voucherWord;

    /**
     * 打印标题
     */
    private String printTitle;

    /**
     * 是否是默认[0:否，1：默认]
     */
    private Boolean defaultFlag;

    /**
     * 使用计数
     */
    private Integer useCount;
}