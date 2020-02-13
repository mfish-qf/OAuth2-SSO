package com.qf.sso.core.common;

/**
 * @author qiufeng
 * @date 2020/2/10 19:32
 */
public class SerConstant {
    public static final String AUTH_CODE = "auth_code:";
    public static final String ACCESS_TOKEN = "access_token:";
    public static final String REFRESH_TOKEN = "refresh_token:";
    public static final String USER_PASSWORD = "user_password:";
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
            return LoginType.微信认证;
        }

        public static LoginType getLoginType(int index) {
            for (LoginType type : LoginType.values()) {
                if (type.getIndex() == index) {
                    return type;
                }
            }
            return LoginType.微信认证;
        }

        public int getIndex() {
            return index;
        }
    }
}
