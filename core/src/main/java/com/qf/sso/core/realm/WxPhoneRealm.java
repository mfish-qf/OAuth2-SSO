package com.qf.sso.core.realm;

import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.exception.OAuthValidateException;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;

import javax.annotation.Resource;


/**
 * 微信手机号登陆验证
 * @author qiufeng
 * @date 2021/10/26 15:41
 */
public class WxPhoneRealm extends BaseRealm {
    @Resource
    LoginService loginService;

    @Override
    protected AuthenticationInfo buildAuthenticationInfo(SSOUser user, AuthenticationToken authenticationToken, boolean newUser) {
        String sessionKey = String.valueOf((char[]) authenticationToken.getCredentials());
        String openId = loginService.getOpenIdBySessionKey(sessionKey);
        if (StringUtils.isEmpty(openId)) {
            throw new OAuthValidateException(SerConstant.INVALID_WX_ID_DESCRIPTION);
        }
        AuthenticationInfo authorizationInfo = new SimpleAuthenticationInfo(
                user.getId(), sessionKey, getName());
        return authorizationInfo;
    }
}
