package com.qf.sso.core.credentials;

import com.qf.sso.core.cache.redis.RedisPrefix;
import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.realm.MyUsernamePasswordToken;
import com.qf.sso.core.service.LoginService;
import com.qf.sso.core.service.UserService;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiufeng
 * @date 2020/2/10 19:48
 */
public class MyHashedCredentialsMatcher extends HashedCredentialsMatcher {
    @Autowired
    LoginService loginService;

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        MyUsernamePasswordToken myToken = (MyUsernamePasswordToken) token;
        boolean matches = super.doCredentialsMatch(token, info);
        return loginService.retryLimit(myToken.getUserId(), matches);
    }

}
