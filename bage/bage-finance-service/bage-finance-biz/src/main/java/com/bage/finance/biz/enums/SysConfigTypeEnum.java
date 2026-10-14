package com.bage.finance.biz.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置类型
 */
public enum SysConfigTypeEnum {
    SEND_SMS_CODE_TEMPLATE("SEND_SMS_CODE_TEMPLATE", "发送短信验证码模板");

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

    SysConfigTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(String code) {
        for (SysConfigTypeEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getMessage();
            }
        }
        return null;
    }

    /**
     * 获取所有枚举值列表
     * @return
     */
    public static Map<String, String> list() {
        Map<String, String> result = new HashMap<>();
        for (SysConfigTypeEnum ele : values()) {
            result.put(ele.getCode(), ele.getMessage());
        }
        return result;
    }
}
