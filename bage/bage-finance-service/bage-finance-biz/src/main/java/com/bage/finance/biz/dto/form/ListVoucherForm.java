package com.bage.finance.biz.dto.form;

import com.bage.common.dto.PageHelperRequest;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ListVoucherForm extends PageHelperRequest {

    /**
     * 备注
     */
    private String notes;

    /**
     * 用户id
     */
    private Long memberId;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 凭证字配置id
     */
    private Long voucherWordConfigId;

    /**
     * 开始凭证号
     */
    private Integer beginVoucherNumber;

    /**
     * 结束凭证号
     */
    private Integer endVoucherNumber;

    /**
     * 开始凭证日期
     */
    private Date beginVoucherDate;

    /**
     * 结束凭证日期
     */
    private Date endVoucherDate;

    /**
     * 开始凭证总金额
     */
    private BigDecimal beginTotalAmount;

    /**
     * 结束凭证总金额
     */
    private BigDecimal endTotalAmount;

    /**
     * 科目id
     */
    private Long subjectId;

    /**
     * 排序规则 ，0：凭证号升序，1：凭证号降序，2：凭证日期升序，3：凭证日期降序
     */
    private Integer sortRule = 0;
}
