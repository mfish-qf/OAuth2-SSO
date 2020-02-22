package com.qf.sso.core.realm;

import com.qf.sso.core.cache.temp.UserTempCache;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author qiufeng
 * @date 2020/2/11 9:33
 */
public class UserPasswordRealm extends AuthorizingRealm {
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
            throw new UnknownAccountException();//没找到帐号
        }
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getId(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getId() + user.getSalt()),
                getName()  //realm name
        );
        return authenticationInfo;
    }
}
