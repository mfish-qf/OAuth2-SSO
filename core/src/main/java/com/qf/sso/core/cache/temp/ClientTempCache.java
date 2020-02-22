package com.qf.sso.core.cache.temp;

import com.qf.sso.core.cache.redis.RedisPrefix;
import com.qf.sso.core.dao.ClientDao;
import com.qf.sso.core.model.OAuthClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qiufeng
 * @date 2020/2/16 15:54
 */
@Component
public class ClientTempCache extends BaseTempCache<OAuthClient> {
    @Resource
    ClientDao clientDao;

    @Override
    public String buildKey(String key) {
        return RedisPrefix.buildClientKey(key);
    }

    @Override
    public OAuthClient getFromDB(String key) {
        return clientDao.getClientById(key);
    }
}
