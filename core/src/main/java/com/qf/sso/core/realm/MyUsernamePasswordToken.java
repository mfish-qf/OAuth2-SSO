package com.qf.sso.core.realm;

import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.model.SSOUser;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @author qiufeng
 * @date 2020/2/10 19:28
 */
public class MyUsernamePasswordToken extends UsernamePasswordToken {

    //用户ID
    private SSOUser userInfo;
    private boolean isNew;

    //登录类型
    private SerConstant.LoginType loginType = SerConstant.LoginType.密码登录;

    public MyUsernamePasswordToken(String username, String password) {
        super(username, password);
    }

    public MyUsernamePasswordToken(String username, String password, boolean rememberMe) {
        super(username, password, rememberMe);
    }


    public SerConstant.LoginType getLoginType() {
        return loginType;
    }

    public MyUsernamePasswordToken setLoginType(SerConstant.LoginType loginType) {
        this.loginType = loginType;
        return this;
    }
    public SSOUser getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(SSOUser userInfo) {
        this.userInfo = userInfo;
    }
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
