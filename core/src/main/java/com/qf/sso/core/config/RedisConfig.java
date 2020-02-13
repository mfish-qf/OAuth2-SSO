package com.qf.sso.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

/**
 * @author qiufeng
 * @date 2020/2/11 18:05
 */
@Configuration
public class RedisConfig {
    /**
     * 通用redisTemplate采用GenericJackson2JsonRedisSerializer序列化value
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Lazy RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置key的序列化方式，采用StringRedisSerializer
        GenericToStringSerializer<String> keySerializer = new GenericToStringSerializer<>(String.class);
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        // 设置value的序列化方式，采用Jackson2JsonRedisSerializer
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 处理String类型键值对
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(@Lazy RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * redis存储session序列化方式使用GenericJackson2JsonRedisSerializer会造成反序列化失败
     * 单独定义template
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "sessionRedisTemplate")
    public RedisTemplate<String, Object> sessionRedisTemplate(@Lazy RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate sessionRedisTemplate = new RedisTemplate();
        sessionRedisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置key的序列化方式，采用StringRedisSerializer
        GenericToStringSerializer<String> keySerializer = new GenericToStringSerializer<>(String.class);
        sessionRedisTemplate.setKeySerializer(keySerializer);
        sessionRedisTemplate.setHashKeySerializer(keySerializer);
        sessionRedisTemplate.afterPropertiesSet();
        return sessionRedisTemplate;
    }

}
