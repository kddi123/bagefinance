package com.bage.finance.biz.enums;

/**
 * mq消息状态
 */
public enum MqMsgStatusEnum {
    WAIT(0, "待处理"),
    SUCCESS(1, "消费成功"),
    FAIL(2, "消费失败");

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

    MqMsgStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (MqMsgStatusEnum ele : values()) {
            if (ele.getCode() == code) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
