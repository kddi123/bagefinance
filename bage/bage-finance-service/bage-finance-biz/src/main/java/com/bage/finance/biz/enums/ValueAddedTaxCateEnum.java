package com.bage.finance.biz.enums;

/**
 * 增值税种类枚举值
 */
public enum ValueAddedTaxCateEnum {
    SMALL((byte) 0, "小规模纳税人"),
    COMMONLY((byte) 1, "一般纳税人");

    private String message;
    private Byte code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    ValueAddedTaxCateEnum(Byte code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(byte code) {
        for (ValueAddedTaxCateEnum ele : values()) {
            if (ele.getCode() == code) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
