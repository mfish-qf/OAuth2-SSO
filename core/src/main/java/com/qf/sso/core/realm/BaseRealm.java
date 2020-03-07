package com.qf.sso.core.realm;

import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author qiufeng
 * @date 2020/2/26 17:23
 */
public abstract class BaseRealm extends AuthorizingRealm {
    @Autowired
    UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //        String account = (String) principalCollection.getPrimaryPrincipal();
        //        if(user == null) {
        //            throw new UnknownAccountException();//没找到帐号
        //        }
        //        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //暂时不加权限
        return new SimpleAuthorizationInfo();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String account = (String) authenticationToken.getPrincipal();
        SSOUser user = userService.getUserByAccount(account);
        if (user == null) {
            //未找到账号
            throw new UnknownAccountException();
        }
        //不同登录方式采用不同的AuthenticationInfo构建方式
        return buildAuthenticationInfo(user, authenticationToken);
    }

    protected abstract AuthenticationInfo buildAuthenticationInfo(SSOUser user, AuthenticationToken authenticationToken);
}
