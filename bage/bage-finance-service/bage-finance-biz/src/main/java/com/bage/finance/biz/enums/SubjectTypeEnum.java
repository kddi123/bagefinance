package com.bage.finance.biz.enums;

/**
 * 科目类型
 */
public enum SubjectTypeEnum {
    SYSTEM(0, "系统科目"),
    CUSTOM(1, "自定义科目");

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

    SubjectTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (SubjectTypeEnum ele : values()) {
            if (ele.getCode() == code) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
