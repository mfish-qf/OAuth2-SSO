package com.qf.sso.core.realm;

import com.qf.sso.core.common.SerConstant;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @author qiufeng
 * @date 2020/2/10 19:28
 */
public class MyUsernamePasswordToken extends UsernamePasswordToken {

    //用户ID
    private String userId;

    //登录类型
    private SerConstant.LoginType loginType = SerConstant.LoginType.密码登录;

    public MyUsernamePasswordToken(String username, String password) {
        super(username, password);
    }

    public MyUsernamePasswordToken(String username, String password, boolean rememberMe) {
        super(username, password, rememberMe);
    }

    public String getUserId() {
        return userId;
    }

    public MyUsernamePasswordToken setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public SerConstant.LoginType getLoginType() {
        return loginType;
    }

    public MyUsernamePasswordToken setLoginType(SerConstant.LoginType loginType) {
        this.loginType = loginType;
        return this;
    }
}
