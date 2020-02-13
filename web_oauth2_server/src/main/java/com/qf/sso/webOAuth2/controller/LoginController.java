package com.qf.sso.webOAuth2.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author qiufeng
 * @date 2020/2/11 11:13
 */
@Api(tags = "登录")
@Controller
public class LoginController {
    @ApiOperation("跳转登录页面")
    @RequestMapping("/login")
    public String loginPage() {
        return "login";
    }
}
