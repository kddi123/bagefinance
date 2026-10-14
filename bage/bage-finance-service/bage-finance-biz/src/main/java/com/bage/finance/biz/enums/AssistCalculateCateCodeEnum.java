package com.bage.finance.biz.enums;

/**
 * 辅助核算类别编码枚举值（系统级别）
 */
public enum AssistCalculateCateCodeEnum {
    CUSTOMER("CUSTOMER", "客户"),
    SUPPLIER("SUPPLIER", "供应商"),
    EMPLOYEE("EMPLOYEE", "职员"),
    DEPARTMENT("DEPARTMENT", "部门"),
    PROJECT("PROJECT", "项目"),
    INVENTORY("INVENTORY", "存货"),
    CASH_FLOW("CASH_FLOW", "现金流"),
    CUSTOM("CUSTOM", "自定义");

    private String message;
    private String code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    AssistCalculateCateCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(String code) {
        for (AssistCalculateCateCodeEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
