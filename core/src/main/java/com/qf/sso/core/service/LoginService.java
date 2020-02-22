package com.qf.sso.core.service;

import com.qf.sso.core.common.CheckWithResult;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;


/**
 * @author qiufeng
 * @date 2020/2/15 16:09
 */
public interface LoginService {
    boolean getLogin(Model model, HttpServletRequest request);

    boolean postLogin(Model mode, HttpServletRequest request);

    boolean login(Model model, HttpServletRequest request);

    CheckWithResult<String> login(HttpServletRequest request);

    boolean retryLimit(String userId, boolean matches);
}
