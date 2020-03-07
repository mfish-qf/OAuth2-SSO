package com.qf.sso.appOAuth2.service.impl;

import com.qf.sso.appOAuth2.model.WeChatToken;
import com.qf.sso.appOAuth2.service.WeChatService;
import com.qf.sso.core.cache.redis.RedisPrefix;
import com.qf.sso.core.cache.temp.OpenIdTempCache;
import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.common.HttpRequestUtil;
import com.qf.sso.core.model.AccessToken;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author qiufeng
 * @date 2020/2/28 17:34
 */
@Service
@Slf4j
public class WeChatServiceImpl implements WeChatService {
    @Value("${weChat.url}")
    private String url = "";
    @Value("${weChat.appId}")
    private String appId = "";
    @Value("${weChat.secret}")
    private String secret = "";
    @Value("${oauth2.expire.token}")
    private long tokenExpire = 21600;
    @Value("${oauth2.expire.token}")
    private long reTokenExpire = 604800;

    @Resource
    OpenIdTempCache openIdTempCache;
    @Resource
    UserService userService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 构造微信code2session接口链接
     *
     * @param code
     * @return
     */
    private String buildWeChatUrl(String code) {
        return url + "?appid=" + appId + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
    }

    @Override
    public CheckWithResult<Map<String, String>> getSession(String code) {
        String url = buildWeChatUrl(code);
        CheckWithResult<Map<String, String>> result = null;
        try {
            result = HttpRequestUtil.getMessage(url);
        } catch (IOException e) {
            log.error("错误:请求微信服务端失败", e);
        }
        if (result == null) {
            return new CheckWithResult<Map<String, String>>().setSuccess(false).setMsg("错误:获取微信session出错");
        }
        return result;
    }

    @Override
    public String getUserId(String openId) {
        return openIdTempCache.getCacheInfo(openId);
    }

    @Override
    public boolean bindWeChat(String openId, String userId) {
        SSOUser user = new SSOUser();
        user.setOpenid(openId);
        user.setId(userId);
        CheckWithResult<SSOUser> result = userService.update(user);
        return result.isSuccess();
    }

    @Override
    public WeChatToken buildWeChatToken(String openId, String sessionKey, String userId) {
        WeChatToken weChatToken = new WeChatToken();
        weChatToken.setOpenid(openId);
        weChatToken.setSession_key(sessionKey);
        weChatToken.setAccess_token(UUID.randomUUID().toString());
        weChatToken.setRefresh_token(UUID.randomUUID().toString());
        weChatToken.setUserId(userId);
        SSOUser user = userService.getUserById(userId);
        weChatToken.setAccount(user.getAccount());
        weChatToken.setExpires_in(tokenExpire);
        setToken(weChatToken);
        setRefreshToken(weChatToken);
        return weChatToken;
    }

    @Override
    public AccessToken convertToken(WeChatToken weChatToken) {
        //重新copy屏蔽不像外返回的属性
        return new AccessToken().setAccess_token(weChatToken.getAccess_token())
                .setRefresh_token(weChatToken.getRefresh_token())
                .setExpires_in(weChatToken.getExpires_in());
    }

    @Override
    public void setToken(WeChatToken token) {
        redisTemplate.opsForValue().set(RedisPrefix.buildAccessTokenKey(token.getAccess_token()), token, token.getExpires_in(), TimeUnit.SECONDS);
    }

    @Override
    public void delToken(String token) {
        redisTemplate.delete(RedisPrefix.buildAccessTokenKey(token));
    }

    @Override
    public WeChatToken getToken(String token) {
        return (WeChatToken) redisTemplate.opsForValue().get(RedisPrefix.buildAccessTokenKey(token));
    }

    @Override
    public void setRefreshToken(WeChatToken token) {
        redisTemplate.opsForValue().set(RedisPrefix.buildRefreshTokenKey(token.getRefresh_token()), token, reTokenExpire, TimeUnit.SECONDS);
    }

    @Override
    public void updateRefreshToken(WeChatToken token) {
        String key = RedisPrefix.buildRefreshTokenKey(token.getRefresh_token());
        Long expire = redisTemplate.getExpire(key);
        redisTemplate.opsForValue().set(key, token, expire, TimeUnit.SECONDS);
    }

    @Override
    public WeChatToken getRefreshToken(String token) {
        return (WeChatToken) redisTemplate.opsForValue().get(RedisPrefix.buildRefreshTokenKey(token));
    }

    @Override
    public void delRefreshToken(String token) {
        redisTemplate.delete(RedisPrefix.buildRefreshTokenKey(token));
    }
}
