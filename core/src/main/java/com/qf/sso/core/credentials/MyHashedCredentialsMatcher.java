package com.qf.sso.core.credentials;

import com.qf.sso.core.realm.MyUsernamePasswordToken;
import com.qf.sso.core.service.LoginService;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

import javax.annotation.Resource;

/**
 * @author qiufeng
 * @date 2020/2/10 19:48
 */
public class MyHashedCredentialsMatcher extends HashedCredentialsMatcher {
    @Resource
    LoginService loginService;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        MyUsernamePasswordToken myToken = (MyUsernamePasswordToken) token;
        boolean matches = super.doCredentialsMatch(token, info);
        return loginService.retryLimit(myToken.getUserId(), matches);
    }

}
