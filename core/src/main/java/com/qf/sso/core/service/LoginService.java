package com.qf.sso.core.service;

import com.qf.sso.core.common.CheckWithResult;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;


/**
 * @author qiufeng
 * @date 2020/2/15 16:09
 */
public interface LoginService {
    /**
     * 请求登录
     * @param model
     * @param request
     * @return
     */
    boolean getLogin(Model model, HttpServletRequest request);

    /**
     * 提交登录数据
     * @param mode
     * @param request
     * @return
     */
    boolean postLogin(Model mode, HttpServletRequest request);

    /**
     * 登录
     * @param request
     * @return
     */
    CheckWithResult<String> login(HttpServletRequest request);

    /**
     * 登录重试计数
     * @param userId
     * @param matches
     * @return
     */
    boolean retryLimit(String userId, boolean matches);

    /**
     * 发送短信
     * @param phone
     * @param msg
     */
    void sendMsg(String phone, String msg);

    /**
     * 保存短信验证码
     * @param phone
     * @param code
     */
    void saveSmsCode(String phone, String code);

    /**
     * 获取短信验证码
     * @param phone
     * @return
     */
    String getSmsCode(String phone);
    /**
     * 保存短信倒计时信息
     * @param phone
     */
    void saveSmsCodeTime(String phone);

    /**
     * 获取短信验证码倒计时时间
     * @param phone
     * @return
     */
    long getSmsCodeTime(String phone);
}
