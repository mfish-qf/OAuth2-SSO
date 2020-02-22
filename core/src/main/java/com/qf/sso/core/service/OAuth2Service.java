package com.qf.sso.core.service;

import com.qf.sso.core.model.AuthorizationCode;
import com.qf.sso.core.model.RedisAccessToken;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;

/**
 * @author qiufeng
 * @date 2020/2/13 12:51
 */
public interface OAuth2Service {
    AuthorizationCode buildCode(OAuthAuthzRequest request) throws OAuthSystemException;

    void setCode(AuthorizationCode code);

    void delCode(String code);

    AuthorizationCode getCode(String code);

    RedisAccessToken buildToken(OAuthTokenRequest request) throws OAuthSystemException, InvocationTargetException, IllegalAccessException;

    RedisAccessToken code2Token(OAuthTokenRequest request, AuthorizationCode code) throws OAuthSystemException, InvocationTargetException, IllegalAccessException;

    RedisAccessToken refresh2Token(RedisAccessToken token) throws OAuthSystemException;

    void setToken(RedisAccessToken token);

    void delToken(String token);

    RedisAccessToken getToken(String token);

    void setRefreshToken(RedisAccessToken token);

    void updateRefreshToken(RedisAccessToken token);

    RedisAccessToken getRefreshToken(String token);

    void delRefreshToken(String token);
}
