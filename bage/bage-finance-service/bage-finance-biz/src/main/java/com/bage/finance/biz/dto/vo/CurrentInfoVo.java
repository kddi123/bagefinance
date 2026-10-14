package com.bage.finance.biz.dto.vo;


import lombok.Data;

@Data
public class CurrentInfoVo {
    /**
     *昵称
     */
    private String name;
    /**
     *头像
     */
    private String avatar;
    /**
     *邮箱地址
     */
    private String email;
}