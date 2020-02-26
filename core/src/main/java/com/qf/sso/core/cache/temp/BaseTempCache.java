package com.qf.sso.core.cache.temp;

import com.qf.sso.core.cache.redis.RedisPrefix;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author qiufeng
 * @date 2020/2/14 17:20
 */
@Data
public abstract class BaseTempCache<T> {
    @Resource
    RedisTemplate<String,Object> redisTemplate;

    /**
     * 构建key
     * @param key
     * @return
     */
    public abstract String buildKey(String key);

    /**
     * 缓存中不存在，从数据库中获取
     * @param key
     * @return
     */
    public abstract T getFromDB(String key);

    /**
     * 获取缓存信息
     * redis存在直接返回，redis中不存在访问数据库
     * 5分钟内在请求数据库超过10次，不在访问数据库直接返回null
     * 获取到数据存入到redis缓存中，持久化一周
     * （后期可将时间做成配置）
     *
     * @param param
     * @return
     */
    public T getCacheInfo(String param) {
        String key = buildKey(param);
        T value = (T) redisTemplate.opsForValue().get(key);
        if (value != null) {
            return value;
        }
        RedisAtomicLong ral = new RedisAtomicLong(RedisPrefix.buildAtomicCountKey(key)
                , redisTemplate.getConnectionFactory());
        long inc = ral.getAndIncrement();
        if (inc == 0) {
            ral.expire(5, TimeUnit.MINUTES);
        }
        if (inc >= 10) {
            return null;
        }
        value = getFromDB(param);
        if (value == null) {
            return null;
        }
        setCacheInfo(key, value);
        return value;
    }

    /**
     * 更新缓存
     * @param key 传入键未拼接前缀
     * @param t
     */
    public void updateCacheInfo(String key, T t) {
        setCacheInfo(buildKey(key), t);
    }

    /**
     * 设置缓存
     * @param key 已拼接前缀
     * @param value
     */
    public void setCacheInfo(String key, T value) {
        redisTemplate.opsForValue().set(key, value, 7, TimeUnit.DAYS);
    }

}
