package com.qf.sso.appOAuth2.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.qf.sso.appOAuth2.model.WeChatToken;
import com.qf.sso.appOAuth2.service.WeChatService;
import com.qf.sso.appOAuth2.validator.WeChatTokenValidator;
import com.qf.sso.core.annotation.LogAnnotation;
import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.common.Utils;
import com.qf.sso.core.exception.OAuthValidateException;
import com.qf.sso.core.model.AccessToken;
import com.qf.sso.core.model.UserInfo;
import com.qf.sso.core.service.LoginService;
import com.qf.sso.core.service.OAuth2Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qiufeng
 * @date 2020/2/28 13:52
 */
@Api(tags = "小程序接口")
@RestController
@RequestMapping("/wx")
@Slf4j
public class WeChatController {
    @Resource
    WeChatService weChatService;
    @Resource
    LoginService loginService;
    @Resource
    OAuth2Service oAuth2Service;
    @Resource
    WeChatTokenValidator weChatTokenValidator;
    @Resource
    WxMaService wxMaService;

    @GetMapping("/bind/check")
    @ApiOperation("检查微信是否绑定 如果已绑定返回accessToken")
    @ApiImplicitParams({
            @ApiImplicitParam(name = SerConstant.QR_CODE, value = "微信认证code", paramType = "query", required = true)
    })
    public AccessToken checkBind(String code) {
        WxMaJscode2SessionResult session = getSession(code);
        String openid = session.getOpenid();
        String userId = weChatService.getUserIdByOpenId(openid);
        if (!StringUtils.isEmpty(userId)) {
            return new AccessToken(weChatService.buildWeChatToken(openid, session.getSessionKey(), userId));
        }
        log.error("微信:" + openid + ",未绑定成功");
        throw new OAuthValidateException("错误:微信未绑定");
    }

    /**
     * 获取微信session
     *
     * @param code 微信认证code
     * @return
     */
    private WxMaJscode2SessionResult getSession(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new OAuthValidateException("错误:认证code不允许为空");
        }
        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            if (session == null) {
                throw new OAuthValidateException("错误:未获取到用户session");
            }
            String openid = session.getOpenid();
            if (StringUtils.isEmpty(openid)) {
                throw new OAuthValidateException("错误:未获取openid");
            }
            if (StringUtils.isEmpty(session.getSessionKey())) {
                throw new OAuthValidateException("错误:未获取sessionKey");
            }
            return session;
        } catch (WxErrorException e) {
            throw new OAuthValidateException(e.getMessage());
        }
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
        WxMaJscode2SessionResult session = getSession(code);
        String openid = session.getOpenid();
        if (StringUtils.isEmpty(openid)) {
            throw new OAuthValidateException("错误:未获取openid");
        }
        CheckWithResult<String> loginResult = loginService.login(request);
        if (loginResult.isSuccess() && weChatService.bindWeChat(openid, loginResult.getResult())) {
            return weChatService.convertToken(weChatService
                    .buildWeChatToken(openid, session.getSessionKey(), loginResult.getResult()));
        }
        throw new OAuthValidateException("错误:微信账号绑定失败");
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

    @PostMapping("/login/pwd")
    @ApiOperation("通过密码绑定账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = SerConstant.QR_CODE, value = "微信认证code", paramType = "query", required = true),
            @ApiImplicitParam(name = SerConstant.NICKNAME, value = "微信昵称", paramType = "query"),
            @ApiImplicitParam(name = SerConstant.PHONE, value = "手机", paramType = "query", required = true),
            @ApiImplicitParam(name = SerConstant.PASSWORD, value = "短信验证码", paramType = "query", required = true)
    })
    public AccessToken pwdLogin(String code, String nickname, String phone, String password) {
        WxMaJscode2SessionResult session = getSession(code);
        String openid = session.getOpenid();
        String userId = weChatService.getUserIdByOpenId(openid);
        if (!StringUtils.isEmpty(userId)) {
            return new AccessToken(weChatService
                    .buildWeChatToken(openid, session.getSessionKey(), userId));
        }
        CheckWithResult<String> loginResult = loginService.login(phone, password, SerConstant.LoginType.短信登录, "false");
        if (StringUtils.isEmpty(nickname)) {
            nickname = Utils.phoneMasking(phone);
        }
        if (loginResult.isSuccess() && weChatService.bindWeChat(openid, loginResult.getResult(), nickname)) {
            return new AccessToken(weChatService
                    .buildWeChatToken(openid, session.getSessionKey(), loginResult.getResult()));
        }
        throw new OAuthValidateException("错误:绑定微信失败");
    }


    @ApiOperation("获取sessionKey 与手机登录联合使用")
    @GetMapping("/session")
    @ApiImplicitParams({
            @ApiImplicitParam(name = SerConstant.QR_CODE, value = "微信认证code", paramType = "query", required = true)
    })
    public Map<String, String> getSessionKey(String code) {
        WxMaJscode2SessionResult session = getSession(code);
        loginService.sessionKeyTempCache(session.getSessionKey(), session.getOpenid());
        Map<String, String> map = new HashMap<>();
        map.put("sessionKey", session.getSessionKey());
        return map;
    }

    @ApiOperation("通过手机号绑定账号 先调用/sessionKey方法")
    @PostMapping("/login/phone")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionKey", value = "微信登录返回的sessionKey", paramType = "query", required = true),
            @ApiImplicitParam(name = "nickname", value = "微信昵称", paramType = "query"),
            @ApiImplicitParam(name = "encryptedData", value = "微信获取手机号数据", paramType = "query", required = true),
            @ApiImplicitParam(name = "iv", value = "微信获取手机号iv", paramType = "query", required = true)
    })
    public AccessToken phoneLogin(String sessionKey, String nickname, String encryptedData, String iv) {
        String openid = loginService.getOpenIdBySessionKey(sessionKey);
        if (StringUtils.isEmpty(openid)) {
            log.error("sessionKey:" + sessionKey + ",手机号绑定失败");
            throw new OAuthValidateException("错误:sessionKey有误");
        }
        String userId = weChatService.getUserIdByOpenId(openid);
        if (!StringUtils.isEmpty(userId)) {
            return new AccessToken(weChatService.buildWeChatToken(openid, sessionKey, userId));
        }
        WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
        String phone = phoneNoInfo.getPhoneNumber();
        CheckWithResult<String> loginResult = loginService.login(phone, sessionKey, SerConstant.LoginType.微信登录, "false");
        if (StringUtils.isEmpty(nickname)) {
            nickname = Utils.phoneMasking(phone);
        }
        if (loginResult.isSuccess() && weChatService.bindWeChat(openid, loginResult.getResult(), nickname)) {
            return new AccessToken(weChatService.buildWeChatToken(openid, sessionKey, loginResult.getResult()));
        }
        throw new OAuthValidateException("错误:绑定微信失败");
    }
}
