package com.qf.sso.core.service;

import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.model.redis.UserCipher;

/**
 * @author qiufeng
 * @date 2020/2/13 16:50
 */
public interface UserService {
    CheckWithResult<UserCipher> changePassword(String userId, String newPassword);
    CheckWithResult<SSOUser> update(SSOUser user);
}
