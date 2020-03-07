package com.qf.sso.core.credentials;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * @author qiufeng
 * @date 2020/2/25 16:49
 */
public class QRCodeCredentialsMatcher extends SimpleCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        return super.doCredentialsMatch(authenticationToken, authenticationInfo);
    }
}