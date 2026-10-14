package com.bage.finance.biz.dto.vo;


import lombok.Data;

@Data
public class GenerateMpRegCodeVo {
    /**
     * 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天）。
     */
    private Integer expireSeconds;


    /**
     * 二维码图片解析后的地址，开发者可根据该地址自行生成需要的二维码图片
     */
    private String url;

    /**
     * 二维码地址
     */
    private String qrCodeUrl;

    /**
     * 获取的二维码ticket，凭借此 ticket 可以在有效时间内换取二维码。
     */
    private String ticket;
}
