package com.qf.sso.webOAuth2.controller;

import com.qf.sso.core.annotation.LogAnnotation;
import com.qf.sso.core.cache.UserTokenCache;
import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.exception.OAuthValidateException;
import com.qf.sso.core.model.RedisAccessToken;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.model.UserInfo;
import com.qf.sso.core.service.UserService;
import com.qf.sso.core.validator.AccessTokenValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;

/**
 * @author qiufeng
 * @date 2020/2/17 18:49
 */
@Api(tags = "用户信息")
@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Resource
    AccessTokenValidator accessTokenValidator;
    @Resource
    UserService userService;
    @Resource
    UserTokenCache userTokenCache;

    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "认证token，header和access_token参数两种方式任意一种即可，格式为Bearer+token组合，例如Bearer39a5304bc77c655afbda6b967e5346fa", paramType = "header"),
            @ApiImplicitParam(name = "access_token", value = "token值 header和access_token参数两种方式任意一种即可", paramType = "query")
    })
    @LogAnnotation("getUser")
    public UserInfo getUserInfo(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        CheckWithResult<RedisAccessToken> result = accessTokenValidator.validate(request, null);
        if (!result.isSuccess()) {
            throw new OAuthValidateException(result.getMsg());
        }
        SSOUser user = userService.getUserById(result.getResult().getUserId());
        if (user == null) {
            throw new OAuthValidateException("错误:未获取到用户信息！");
        }

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfo, user);
        return userInfo;
    }

    @ApiOperation("用户登出")
    @GetMapping("/revoke")
    public CheckWithResult<String> revoke() {
        Subject subject = SecurityUtils.getSubject();
        if (subject == null) {
            String error = "错误:未获取到用户登录状态!";
            return new CheckWithResult<String>().setSuccess(false).setMsg(error).setResult(error);
        }
        String userId = (String) subject.getPrincipal();
        userTokenCache.delUserDevice(SerConstant.DeviceType.Web, userId);
        subject.logout();
        return new CheckWithResult<String>().setMsg("成功登出!").setResult("登出成功!");
    }
}
