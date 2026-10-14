package com.bage.finance.biz.enums;

/**
 * 会计准则
 */
public enum AccountingStandardEnum {
    XQY((byte) 0, "小企业会计准则"),
    QY((byte) 1, "企业会计准则"),
    MJFYL((byte) 2, "民间非盈利组织会计制度"),
    NMZYHZS((byte) 3, "农民专业合作社财务会计制度");

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

    AccountingStandardEnum(Byte code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(byte code) {
        for (AccountingStandardEnum ele : values()) {
            if (ele.getCode() == code) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
