package com.bage.finance.biz.constant;

public class RedisKeyConstant {
    //图形验证码
    public static final String GRAPHIC_VERIFICATION_CODE = "GRAPHIC_VERIFICATION_CODE:";

    /**
     * 短信验证码
     */
    public static final String SMS_CODE = "SMS_CODE:";

    public static final String REG_CODE_KEY = "REG_CODE:";

    public static final String CLIENT_TOKEN_KEY = "CLIENT_TOKEN_KEY:";

    /**
     * 修改菜单锁
     */
    public static final String CHANGE_MENU_LOCK = "CHANGE_MENU_LOCK";

    /**
     * 修改资源锁
     */
    public static final String CHANGE_RESOURCE_LOCK = "CHANGE_RESOURCE_LOCK";

    //用户图形验证码（登录之后的）
    public static final String USER_GRAPHIC_VERIFICATION_CODE = "USER_GRAPHIC_VERIFICATION_CODE:";

    /**
     * 新增或修改手机号
     */
    public static final String PHONE_CHANGE = "PHONE_CHANGE:";

    /**
     * 科目锁
     */
    public static final String SUBJECT_LOCK = "SUBJECT_LOCK:";

    /**
     * 保存凭证锁
     */
    public static final String SAVE_VOUCHER_LOCK = "SAVE_VOUCHER_LOCK:";

    /**
     * 删除文件
     */
    public static final String DELETE_FILE_LOCK = "DELETE_FILE_LOCK:";

    /**
     * 保存文件引用映射锁
     */
    public static final String SAVE_FILE_REF_MAPPING_LOCK = "SAVE_FILE_REF_MAPPING_LOCK:";
}
