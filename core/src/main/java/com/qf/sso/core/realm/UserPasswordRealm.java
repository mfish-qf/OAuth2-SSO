package com.qf.sso.core.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

/**
 * @author qiufeng
 * @date 2020/2/11 9:33
 */
public class UserPasswordRealm extends AuthorizingRealm {
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String staffId = (String)principalCollection.getPrimaryPrincipal();

//        if(user == null) {
//            throw new UnknownAccountException();//没找到帐号
//        }
//        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //暂时不加权限
        return new SimpleAuthorizationInfo();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String staffId = (String)authenticationToken.getPrincipal();
//        Staff_Info user = userService.findOne(staffId);
//        if(user == null) {
//            throw new UnknownAccountException();//没找到帐号
//        }
//        Staff_Password pwd = userService.getPwdByStaffId(staffId);
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                staffId, //用户名
                "password", //密码
                ByteSource.Util.bytes("staffid" +"salt"),
                getName()  //realm name
        );
        return authenticationInfo;
    }
}
