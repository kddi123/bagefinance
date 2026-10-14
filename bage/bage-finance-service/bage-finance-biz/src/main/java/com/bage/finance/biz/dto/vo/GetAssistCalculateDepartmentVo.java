package com.bage.finance.biz.dto.vo;


import lombok.Data;

import java.util.Date;

@Data
public class GetAssistCalculateDepartmentVo extends GetAssistCalculateVo {

    /**
     * 负责人
     */
    private String manager;

    /**
     * 手机
     */
    private String phone;

    /**
     * 成立日期
     */
    private Date startDate;

    /**
     * 撤销日期
     */
    private Date revokeDate;
}