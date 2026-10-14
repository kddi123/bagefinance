package com.bage.finance.biz.enums;

import com.bage.common.exception.BizException;

/**
 * 文件引用类型
 */
public enum FileRefTypeEnum {
    VOUCHER(0, "凭证");

    private String message;
    private Integer code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    FileRefTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(Integer code) {
        for (FileRefTypeEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getMessage();
            }
        }
        throw new BizException("非法编码");
    }

    public static FileRefTypeEnum getEnum(Integer code) {
        for (FileRefTypeEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele;
            }
        }
        throw new BizException("非法编码");
    }
}