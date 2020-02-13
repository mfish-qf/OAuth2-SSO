package com.qf.sso.core.redis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author qiufeng
 * @date 2020/2/11 17:53
 */
@Component
public class RedisCacheManager implements CacheManager {

    @Autowired
    private RedisCache cache;

    @Override
    public Cache<String, Object> getCache(String name) throws CacheException {
        return cache;
    }
}
