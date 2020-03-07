package com.qf.sso.appOAuth2.controller;

import com.qf.sso.appOAuth2.model.WeChatToken;
import com.qf.sso.appOAuth2.service.WeChatService;
import com.qf.sso.appOAuth2.validator.WeChatTokenValidator;
import com.qf.sso.core.annotation.LogAnnotation;
import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.exception.OAuthValidateException;
import com.qf.sso.core.model.AccessToken;
import com.qf.sso.core.model.UserInfo;
import com.qf.sso.core.service.LoginService;
import com.qf.sso.core.service.OAuth2Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author qiufeng
 * @date 2020/2/28 13:52
 */
@Api(tags = "小程序接口")
@RestController
@RequestMapping("/weChat")
public class WeChatController {
    @Resource
    WeChatService weChatService;
    @Resource
    LoginService loginService;
    @Resource
    OAuth2Service oAuth2Service;
    @Resource
    WeChatTokenValidator weChatTokenValidator;

    private static final String OPENID = "openid";
    private static final String SESSION_KEY = "session_key";

    @GetMapping("/checkBind")
    @ApiOperation("检查微信是否绑定 如果已绑定返回accessToken")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.OAUTH_CODE, value = "微信认证code", paramType = "query", required = true)
    })
    public AccessToken checkBind(String code) {
        CheckWithResult<Map<String, String>> result = weChatService.getSession(code);
        if (result.isSuccess()) {
            Map<String, String> map = result.getResult();
            String openid = map.get(OPENID);
            if (StringUtils.isEmpty(openid)) {
                throw new OAuthValidateException("错误:未获取openid");
            }
            String userId = weChatService.getUserId(openid);
            if (!StringUtils.isEmpty(userId)) {
                return weChatService.convertToken(weChatService
                        .buildWeChatToken(openid, map.get(SESSION_KEY), userId));
            }
            throw new OAuthValidateException("错误:微信未绑定");
        }
        throw new OAuthValidateException(result.getMsg());
    }

    @PostMapping("/bind")
    @ApiOperation("绑定账号和微信")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.OAUTH_CODE, value = "微信认证code", paramType = "query", required = true),
            @ApiImplicitParam(name = OAuth.OAUTH_USERNAME, value = "账号，手机，email grant_type=password时必须", paramType = "query", required = true),
            @ApiImplicitParam(name = OAuth.OAUTH_PASSWORD, value = "密码 grant_type=password时必须", paramType = "query", required = true)
    })
    public AccessToken bindWeChat(HttpServletRequest request) {
        String code = request.getParameter(OAuth.OAUTH_CODE);
        CheckWithResult<Map<String, String>> result = weChatService.getSession(code);
        if (result.isSuccess()) {
            Map<String, String> map = result.getResult();
            String openid = map.get(OPENID);
            if (StringUtils.isEmpty(openid)) {
                throw new OAuthValidateException("错误:未获取openid");
            }
            CheckWithResult<String> loginResult = loginService.login(request);
            if (loginResult.isSuccess() && weChatService.bindWeChat(openid, loginResult.getResult())) {
                return weChatService.convertToken(weChatService
                        .buildWeChatToken(openid, map.get(SESSION_KEY), loginResult.getResult()));
            }
        }
        throw new OAuthValidateException(result.getMsg());
    }


    @ApiOperation("获取用户信息")
    @GetMapping("/userInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = OAuth.HeaderType.AUTHORIZATION, value = "认证token，header和access_token参数两种方式任意一种即可，格式为Bearer+token组合，例如Bearer39a5304bc77c655afbda6b967e5346fa", paramType = "header"),
            @ApiImplicitParam(name = OAuth.OAUTH_ACCESS_TOKEN, value = "token值 header和access_token参数两种方式任意一种即可", paramType = "query")
    })
    @LogAnnotation("weChatGetUser")
    public UserInfo getUserInfo(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        CheckWithResult<WeChatToken> result = weChatTokenValidator.validate(request);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        return oAuth2Service.getUserInfo(result.getResult().getUserId());
    }
}
