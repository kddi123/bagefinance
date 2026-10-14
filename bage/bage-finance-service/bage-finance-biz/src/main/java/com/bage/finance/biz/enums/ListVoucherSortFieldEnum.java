package com.bage.finance.biz.enums;

/**
 * 凭证列表查询排序规则
 */
public enum ListVoucherSortFieldEnum {
    VOUCHER_NUMBER_ASC(0, "凭证号升序"),
    VOUCHER_NUMBER_DESC(1, "凭证号降序"),
    VOUCHER_DATE_ASC(2, "凭证日期升序"),
    VOUCHER_DATE_DESC(3, "凭证日期降序");

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

    ListVoucherSortFieldEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(byte code) {
        for (ListVoucherSortFieldEnum ele : values()) {
            if (ele.getCode() == code) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
