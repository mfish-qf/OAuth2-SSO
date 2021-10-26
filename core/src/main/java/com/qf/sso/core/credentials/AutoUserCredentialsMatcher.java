package com.qf.sso.core.credentials;

import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.exception.OAuthValidateException;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.service.UserService;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

import javax.annotation.Resource;

/**
 * @author qiufeng
 * @date 2021/10/26 17:57
 */
public class AutoUserCredentialsMatcher extends SimpleCredentialsMatcher {
    @Resource
    UserService userService;

    protected void insertNewUser(boolean newUser, SSOUser user) {
        if (newUser) {
            CheckWithResult<SSOUser> result = userService.insert(user);
            if (!result.isSuccess()) {
                throw new OAuthValidateException(SerConstant.INVALID_NEW_USER_DESCRIPTION);
            }
        }
    }
}
