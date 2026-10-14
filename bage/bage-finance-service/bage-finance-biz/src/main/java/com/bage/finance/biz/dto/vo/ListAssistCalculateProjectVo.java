package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.util.Date;

/**
 * 辅助核算项目列表
 */
@Data
public class ListAssistCalculateProjectVo extends BaseAssistCalculateVo {
    /**
     * 负责部门
     */
    private String responsibleDepartment;

    /**
     * 责任人
     */
    private String responsiblePerson;

    /**
     * 手机
     */
    private String phone;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;
}