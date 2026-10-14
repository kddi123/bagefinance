package com.bage.finance.biz.dto.vo;


import lombok.Data;

import jakarta.validation.constraints.Min;

import java.util.Date;

@Data
public class ListFileVo {
    /**
     * id
     */
    @Min(value = 0)
    private Long id;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件类型
     */
    private String extension;

    /**
     * 上传日期
     */
    private Date createTime;

    /**
     * 上传人
     */
    private String nickName;

    /**
     * 图片地址
     */
    private String picUrl;
}
