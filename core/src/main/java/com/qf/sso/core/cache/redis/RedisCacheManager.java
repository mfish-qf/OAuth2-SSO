package com.qf.sso.core.cache.redis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qiufeng
 * @date 2020/2/11 17:53
 */
@Component
public class RedisCacheManager implements CacheManager {

    @Resource(name = "redisCache")
    private RedisCache cache;

    @Override
    public Cache<String, Object> getCache(String name) throws CacheException {
        return cache;
    }
}
