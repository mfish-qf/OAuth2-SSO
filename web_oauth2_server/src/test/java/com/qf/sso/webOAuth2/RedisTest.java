package com.qf.sso.webOAuth2;

import com.qf.sso.core.cache.temp.ClientTempCache;
import com.qf.sso.core.model.OAuthClient;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.cache.temp.UserTempCache;
import com.qf.sso.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RedisTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    UserTempCache userTempCache;
    @Autowired
    UserService userService;
    @Autowired
    ClientTempCache clientTempCache;

    @Test
    public void redisOps() {
        redisTemplate.opsForValue().set("test", "11");
    }

    @Test
    public void redisGetSession() {
        Object obj = redisTemplate.opsForValue().get("sso_session56670581-e9b7-44c8-9492-2bf7f5968ba1");
        log.info(obj.toString());
    }

    @Test
    public void getUser() {
        SSOUser user = userService.getUserByAccount("18952006692");
        log.info(user.toString());
    }

    @Test
    public void getClient(){
        OAuthClient client = clientTempCache.getCacheInfo("system");
        log.info(client.toString());
    }
}
