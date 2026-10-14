package com.bage.finance.biz.enums;

/**
 * 辅助核算类别类型
 */
public enum AssistCalculateCateLevelEnum {
    SYSTEM(0, "系统辅助核算类别"),
    CUSTOM(1, "自定义辅助核算类别");

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

    AssistCalculateCateLevelEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (AssistCalculateCateLevelEnum ele : values()) {
            if (ele.getCode() == code) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
