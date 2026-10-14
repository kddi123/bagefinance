package com.bage.finance.biz.dto.form;


import lombok.Data;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Date;

/**
 * 辅助核算（职员）
 */
@Data
public class UpdateAssistCalculateEmployeeForm extends UpdateAssistCalculateForm {
    /**
     * 性别
     */
    @Range(min = 0, max = 2)
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
    @Size(max = 50)
    private String position;

    /**
     * 岗位
     */
    @Size(max = 50)
    private String job;

    /**
     * 手机
     */
    @Pattern(regexp = "^(\\d{11})?$", message = "手机格式不正确")
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