package com.qf.sso.webOAuth2.controller;

import com.qf.sso.core.annotation.LogAnnotation;
import com.qf.sso.core.cache.UserTokenCache;
import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.exception.OAuthValidateException;
import com.qf.sso.core.model.AccessToken;
import com.qf.sso.core.model.AuthorizationCode;
import com.qf.sso.core.model.RedisAccessToken;
import com.qf.sso.core.service.LoginService;
import com.qf.sso.core.service.OAuth2Service;
import com.qf.sso.core.validator.Code2TokenValidator;
import com.qf.sso.core.validator.Refresh2TokenValidator;
import io.swagger.annotations.*;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;

/**
 * @author qiufeng
 * @date 2020/2/17 14:17
 */
@Api(tags = "token获取")
@RestController
@RequestMapping("/oauth2")
public class AccessTokenController {
    @Resource
    Code2TokenValidator code2TokenValidator;
    @Resource
    Refresh2TokenValidator refresh2TokenValidator;
    @Resource
    OAuth2Service oAuth2Service;
    @Resource
    LoginService loginService;
    @Resource
    UserTokenCache userTokenCache;

    @ApiOperation("token获取")
    @PostMapping(value = "/accessToken")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Content-Type", value = "请求类型,必须使用application/x-www-form-urlencoded类型", required = true, paramType = "header", defaultValue = "application/x-www-form-urlencoded"),
            @ApiImplicitParam(name = "grant_type", value = "token获取类型", required = true, paramType = "query"),
            @ApiImplicitParam(name = "client_id", value = "客户端ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "client_secret", value = "客户端秘钥", required = true, paramType = "query"),
            @ApiImplicitParam(name = "redirect_uri", value = "回调地址", required = true, paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "认证code grant_type=authorization_code时必须", paramType = "query"),
            @ApiImplicitParam(name = "username", value = "账号，手机，email grant_type=password时必须", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码 grant_type=password时必须", paramType = "query"),
            @ApiImplicitParam(name = "refresh_token", value = "密码 grant_type=refresh_token时必须", paramType = "query")
    })
    @LogAnnotation("getToken")
    public AccessToken token(HttpServletRequest request) throws OAuthSystemException, OAuthProblemException, InvocationTargetException, IllegalAccessException {
        OAuthTokenRequest tokenRequest = new OAuthTokenRequest(request);
        CheckWithResult<?> result = code2TokenValidator.validateClient(request, null);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        GrantType grantType = GrantType.valueOf(request.getParameter(OAuth.OAUTH_GRANT_TYPE).toUpperCase());
        RedisAccessToken token;
        switch (grantType) {
            case AUTHORIZATION_CODE:
                token = code2Token(request, tokenRequest);
                break;
            case REFRESH_TOKEN:
                token = refresh2Token(request);
                break;
            case PASSWORD:
                token = pwd2Token(request, tokenRequest);
                break;
            default:
                throw new OAuthValidateException(result.getMsg());
        }
        //增加用户登录互斥缓存
        userTokenCache.addUserTokenCache(SerConstant.DeviceType.Web
                , SecurityUtils.getSubject().getSession().getId().toString()
                , token.getUserId(), token.getAccessToken());
        return new AccessToken().setAccess_token(token.getAccessToken()).setExpires_in(token.getExpire()).setRefresh_token(token.getRefreshToken());
    }

    /**
     * 通过code换取token
     *
     * @param request
     * @param tokenRequest
     * @return
     * @throws OAuthSystemException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private RedisAccessToken code2Token(HttpServletRequest request, OAuthTokenRequest tokenRequest) throws OAuthSystemException, IllegalAccessException, InvocationTargetException {
        CheckWithResult<?> result = code2TokenValidator.validateCode(request, null);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        return oAuth2Service.code2Token(tokenRequest, (AuthorizationCode) result.getResult());
    }

    /**
     * 通过refreshtoken获取token
     *
     * @param request
     * @return
     * @throws OAuthSystemException
     */
    private RedisAccessToken refresh2Token(HttpServletRequest request) throws OAuthSystemException {
        CheckWithResult<?> result = refresh2TokenValidator.validateToken(request, null);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        return oAuth2Service.refresh2Token((RedisAccessToken) result.getResult());
    }

    /**
     * 用户名密码直接登录获取token
     *
     * @param request
     * @param tokenRequest
     * @return
     * @throws OAuthSystemException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private RedisAccessToken pwd2Token(HttpServletRequest request, OAuthTokenRequest tokenRequest) throws OAuthSystemException, IllegalAccessException, InvocationTargetException {
        CheckWithResult<?> result = loginService.login(request);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        return oAuth2Service.buildToken(tokenRequest);
    }
}
