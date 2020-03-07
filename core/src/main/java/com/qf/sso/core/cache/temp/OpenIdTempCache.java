package com.qf.sso.core.cache.temp;

import com.qf.sso.core.cache.redis.RedisPrefix;
import com.qf.sso.core.dao.SSOUserDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qiufeng
 * @date 2020/2/29 15:07
 */
@Component
public class OpenIdTempCache extends BaseTempCache<String> {
    @Resource
    SSOUserDao ssoUserDao;

    @Override
    protected String buildKey(String key) {
        return RedisPrefix.buildOpenId2userIdKey(key);
    }

    @Override
    protected String getFromDB(String key) {
        return ssoUserDao.getUserIdByOpenId(key);
    }
}
