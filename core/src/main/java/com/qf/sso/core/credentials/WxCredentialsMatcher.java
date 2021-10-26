package com.qf.sso.core.credentials;

import com.qf.sso.core.realm.MyUsernamePasswordToken;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author qiufeng
 * @date 2021/10/26 17:00
 */
public class WxCredentialsMatcher extends AutoUserCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        MyUsernamePasswordToken myToken = (MyUsernamePasswordToken) authenticationToken;
        insertNewUser(myToken.isNew(), myToken.getUserInfo());
        return true;
    }
}
