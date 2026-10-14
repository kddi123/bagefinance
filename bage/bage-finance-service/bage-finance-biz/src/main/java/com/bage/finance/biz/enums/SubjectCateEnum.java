package com.bage.finance.biz.enums;

/**
 * 科目类别枚举值
 */
public enum SubjectCateEnum {
    PROPERTY(1, "资产"),
    LIABILITIES(2, "负债"),
    EQUITY(3, "权益"),
    COST(4, "成本"),
    INCOME_STATEMENT(5, "损益");

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

    SubjectCateEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (SubjectCateEnum ele : values()) {
            if (ele.getCode() == code) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
