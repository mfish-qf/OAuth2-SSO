package com.qf.sso.core.common;

/**
 * @author qiufeng
 * @date 2020/2/10 19:32
 */
public class SerConstant {
    public static final String ACCOUNT_DELETE_DESCRIPTION = "登录失败:该账号已删除，请联系管理员";
    public static final String ACCOUNT_DISABLE_DESCRIPTION = "登录失败:该帐号已禁用，请联系管理员";
    public static final String INVALID_USER_SECRET_DESCRIPTION = "登录失败:错误的帐号或密码";
    public static final String INVALID_USER_ID_DESCRIPTION = "登录失败:错误的用户ID";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String LOGIN_TYPE = "loginType";
    public static final String ERROR_MSG = "errorMsg";

    /**
     * 登录类型
     */
    public enum LoginType {
        // 微信确认登录
        微信认证("weChat_recognition", 0),
        // 用户名密码登录
        密码("user_password", 1),
        // 短信验证码登录
        一次一密("phone_smsCode", 2),
        // 人脸识别登录
        人脸识别("face_recognition", 3),
        // 扫码
        扫码("qr_code", 4),
        // 同一个session免密登录
        直接("direct", 5);

        private String loginType;
        private int index;

        LoginType(String type, int index) {
            this.loginType = type;
            this.index = index;
        }

        @Override
        public String toString() {
            return loginType;
        }

        public static LoginType getLoginType(String value) {
            for (LoginType type : LoginType.values()) {
                if (type.toString().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return LoginType.密码;
        }

        public static LoginType getLoginType(int index) {
            for (LoginType type : LoginType.values()) {
                if (type.getIndex() == index) {
                    return type;
                }
            }
            return LoginType.密码;
        }

        public int getIndex() {
            return index;
        }

    }

    /**
     * 账号状态
     */
    public enum AccountState {
        删除(0),
        正常(1),
        禁用(2),
        锁定(3);

        private int value;

        AccountState(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.name();
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 设备类型
     */
    public enum DeviceType {
        //浏览器
        Web("0"),
        //手机端
        APP("1");
        private String value;

        DeviceType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
