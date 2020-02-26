package com.qf.sso.core.realm;

import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;

import javax.annotation.Resource;


/**
 * @author qiufeng
 * @date 2020/2/11 9:41
 */
public class PhoneSmsRealm extends BaseRealm {
    @Resource
    LoginService loginService;

    @Override
    protected AuthenticationInfo buildAuthenticationInfo(SSOUser user) {
        String code = loginService.getSmsCode(user.getPhone());
        if (StringUtils.isEmpty(code)) {
            throw new IncorrectCredentialsException(SerConstant.INVALID_USER_SECRET_DESCRIPTION);
        }
        AuthenticationInfo authorizationInfo = new SimpleAuthenticationInfo(
                user.getId(), code, getName());
        return authorizationInfo;
    }
}
