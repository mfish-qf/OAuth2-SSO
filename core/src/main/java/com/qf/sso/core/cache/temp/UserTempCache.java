package com.qf.sso.core.cache.temp;

import com.qf.sso.core.dao.SSOUserDao;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.cache.redis.RedisPrefix;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qiufeng
 * @date 2020/2/14 17:46
 */
@Component
public class UserTempCache extends BaseTempCache<SSOUser> {
    @Resource
    SSOUserDao ssoUserDao;

    @Override
    protected String buildKey(String key) {
        return RedisPrefix.buildUserDetailKey(key);
    }

    @Override
    protected SSOUser getFromDB(String key) {
        return ssoUserDao.getUserByAccount(key);
    }

}
