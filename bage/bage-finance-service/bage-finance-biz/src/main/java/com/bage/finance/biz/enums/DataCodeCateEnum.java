package com.bage.finance.biz.enums;

/**
 * 数据字典分类
 */
public enum DataCodeCateEnum {
    HANG_YE("HANG_YE", "行业"),
    KUAI_JI_ZHUN_ZE("KUAI_JI_ZHUN_ZE", "会计准则");


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

    DataCodeCateEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(String code) {
        for (DataCodeCateEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
