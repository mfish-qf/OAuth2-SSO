package com.qf.sso.core.realm;

import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.common.Utils;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.model.UserInfo;
import com.qf.sso.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * @author qiufeng
 * @date 2020/2/26 17:23
 */
@Slf4j
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
        MyUsernamePasswordToken myToken = (MyUsernamePasswordToken) authenticationToken;
        SSOUser user = userService.getUserByAccount(myToken.getUsername());
        boolean isNew = false;
        if (user == null) {
            //微信登录 短信登录 允许登录时创建账号,其他方式不允许
            if (myToken.getLoginType() != SerConstant.LoginType.微信登录 && myToken.getLoginType() != SerConstant.LoginType.短信登录) {
                log.error("账号:" + myToken.getUsername() + ",未获取到用户信息");
                throw new UnknownAccountException();
            }
            user = buildUser(myToken.getUsername());
            isNew = true;
        }
        //设置用户ID
        myToken.setUserInfo(user);
        myToken.setNew(isNew);
        //不同登录方式采用不同的AuthenticationInfo构建方式
        return buildAuthenticationInfo(user, authenticationToken, isNew);
    }

    /**
     * 构建新用户 只适用于短信登录和微信登录
     *
     * @param phone 手机号
     * @return
     */
    private SSOUser buildUser(String phone) {
        SSOUser userInfo = new SSOUser();
        userInfo.setPhone(phone);
        userInfo.setId(UUID.randomUUID().toString());
        userInfo.setAccount("用户" + phone.substring(7));
        userInfo.setNickname(userInfo.getAccount());
        userInfo.setStatus(1);
        return userInfo;
    }

    protected abstract AuthenticationInfo buildAuthenticationInfo(SSOUser user, AuthenticationToken authenticationToken, boolean newUser);
}
