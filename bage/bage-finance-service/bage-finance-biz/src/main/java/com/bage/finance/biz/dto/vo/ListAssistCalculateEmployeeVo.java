package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.util.Date;

/**
 * 辅助核算职员列表
 */
@Data
public class ListAssistCalculateEmployeeVo extends BaseAssistCalculateVo {
    /**
     * 性别
     */
    private Integer sex;

    /**
     * 部门编码
     */
    private String departmentCode;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 职务
     */
    private String position;

    /**
     * 岗位
     */
    private String job;

    /**
     * 手机
     */
    private String phone;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 入职日期
     */
    private Date startDate;

    /**
     * 离职日期
     */
    private Date departureDate;
}