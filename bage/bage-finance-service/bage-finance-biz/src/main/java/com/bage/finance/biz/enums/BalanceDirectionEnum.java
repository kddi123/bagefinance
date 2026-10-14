package com.bage.finance.biz.enums;

/**
 * 余额方向枚举值
 */
public enum BalanceDirectionEnum {
    BORROW(1, "借"),
    LOAN(2, "贷");

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

    BalanceDirectionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (BalanceDirectionEnum ele : values()) {
            if (ele.getCode() == code) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
