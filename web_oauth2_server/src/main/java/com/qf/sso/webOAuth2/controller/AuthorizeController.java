package com.qf.sso.webOAuth2.controller;

import com.qf.sso.core.model.AuthorizationCode;
import com.qf.sso.core.realm.MyUsernamePasswordToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author qiufeng
 * @date 2020/2/11 11:42
 */
@Api(tags = "认证")
@Controller
@RequestMapping("/oauth2")
public class AuthorizeController {

    @ApiOperation("认证接口")
    @RequestMapping("/authorize")
    public Object authorize(Model model, HttpServletRequest request)
            throws URISyntaxException, OAuthSystemException, OAuthProblemException {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            if (!login(subject, request)) {//登录失败时跳转到登陆页面
                model.addAttribute("test","test1111");
                return "login";
            }
        }
        OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
        AuthorizationCode code = new AuthorizationCode();
        code.setCode("1111");
        code.setRedirectUri("http://www.baidu.com");
        OAuthResponse response = buildCodeResponse(request, code.getCode(), code.getRedirectUri()).buildQueryMessage();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(response.getLocationUri()));
        return new ResponseEntity<Object>(headers, HttpStatus.valueOf(response.getResponseStatus()));
    }

    protected OAuthASResponse.OAuthAuthorizationResponseBuilder buildCodeResponse(HttpServletRequest request, String authorizationCode, String redirectURI) {
        // 进行OAuth响应构建
        OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_MOVED_PERMANENTLY);
        // 设置授权码
        builder.setCode(authorizationCode);
        String state = request.getParameter(OAuth.OAUTH_STATE);
        if (state != null && !"".equals(state)) {
            builder.setParam(OAuth.OAUTH_STATE, state);
        }
        // 构建响应
        return builder.location(redirectURI);
    }

    private boolean login(Subject subject, HttpServletRequest request) {
        if ("get".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return false;
        }
        MyUsernamePasswordToken token = new MyUsernamePasswordToken(username, password);
        try {
            subject.login(token);
            return true;
        } catch (Exception e) {
            request.setAttribute("error", "登录失败:" + e.getClass().getName());
            return false;
        }
    }
}
