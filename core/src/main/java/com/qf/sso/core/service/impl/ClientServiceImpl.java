package com.qf.sso.core.service.impl;

import com.qf.sso.core.cache.temp.ClientTempCache;
import com.qf.sso.core.model.OAuthClient;
import com.qf.sso.core.service.ClientService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author qiufeng
 * @date 2020/2/16 16:11
 */
@Service
public class ClientServiceImpl implements ClientService {
    @Resource
    ClientTempCache clientTempCache;
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据客户端id从缓存和数据库中获取客户端信息
     *
     * @param clientId
     * @return
     */
    @Override
    public OAuthClient getClientById(String clientId) {
        return clientTempCache.getCacheInfo(clientId);
    }
}
