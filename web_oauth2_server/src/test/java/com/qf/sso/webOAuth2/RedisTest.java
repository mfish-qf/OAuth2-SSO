package com.qf.sso.webOAuth2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author qiufeng
 * @date 2020/2/12 12:53
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Test
    public void redisOps(){
        redisTemplate.opsForValue().set("test","11");
    }

    @Test
    public void redisGetSession(){
        Object obj = redisTemplate.opsForValue().get("sso_session56670581-e9b7-44c8-9492-2bf7f5968ba1");
        System.out.println(obj);
    }
}
