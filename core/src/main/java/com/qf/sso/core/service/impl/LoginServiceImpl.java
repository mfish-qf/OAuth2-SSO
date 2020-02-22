package com.qf.sso.core.service.impl;

import com.qf.sso.core.cache.redis.RedisPrefix;
import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.realm.MyUsernamePasswordToken;
import com.qf.sso.core.service.LoginService;
import com.qf.sso.core.service.UserService;
import com.qf.sso.core.validator.GetCodeValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author qiufeng
 * @date 2020/2/15 16:10
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
    @Resource
    UserService userService;
    @Resource(name = "redisTemplate")
    RedisTemplate<String, Object> redisTemplate;
    @Resource
    GetCodeValidator getCodeValidator;
    //允许连续出错时间间隔的最大错误数
    final static int ERROR_COUNT = 5;
    //允许连续出错的时间间隔 单位:分钟
    final static long ERROR_TIME_INTERVAL = 30;

    @Override
    public boolean getLogin(Model model, HttpServletRequest request) {
        validateCode(model, request);
        return false;
    }

    @Override
    public boolean postLogin(Model model, HttpServletRequest request) {
        if (!validateCode(model, request)) {
            return false;
        }
        return login(model, request);
    }

    /**
     * 请求code参数校验
     *
     * @param model
     * @param request
     * @return
     */
    private boolean validateCode(Model model, HttpServletRequest request) {
        CheckWithResult result = getCodeValidator.validateClient(request, null);
        if (!result.isSuccess()) {
            model.addAttribute(SerConstant.ERROR_MSG, result.getMsg());
            return false;
        }
        return true;
    }

    /**
     * 登录用户验证逻辑
     *
     * @param model
     * @param request
     * @return
     */
    @Override
    public boolean login(Model model, HttpServletRequest request) {
        CheckWithResult<String> result = login(request);
        for (Map.Entry<String, String> entry : result.getParam().entrySet()) {
            model.addAttribute(entry.getKey(), entry.getValue());
        }
        return result.isSuccess();
    }

    @Override
    public CheckWithResult<String> login(HttpServletRequest request) {
        SerConstant.LoginType loginType = SerConstant.LoginType.getLoginType(request.getParameter(SerConstant.LOGIN_TYPE));
        String username = request.getParameter(SerConstant.USERNAME);
        String password = request.getParameter(SerConstant.PASSWORD);
        CheckWithResult<String> result = new CheckWithResult<>();
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            result.setSuccess(false).setMsg(SerConstant.INVALID_USER_PASSWORD_DESCRIPTION)
                    .getParam().put(SerConstant.ERROR_MSG, SerConstant.INVALID_USER_PASSWORD_DESCRIPTION);
            return result;
        }
        SSOUser user = userService.getUserByAccount(username);
        if (user == null) {
            result.setSuccess(false).setMsg(SerConstant.INVALID_USER_PASSWORD_DESCRIPTION)
                    .getParam().put(SerConstant.ERROR_MSG, SerConstant.INVALID_USER_PASSWORD_DESCRIPTION);
            return result;
        }
        MyUsernamePasswordToken token = new MyUsernamePasswordToken(username, password, false)
                .setUserId(user.getId()).setLoginType(loginType);
        try {
            SecurityUtils.getSubject().login(token);
            return result;
        } catch (ExcessiveAttemptsException ex) {
            //多次重试错误信息
            result.setSuccess(false).setMsg(ex.getMessage())
                    .getParam().put(SerConstant.ERROR_MSG, ex.getMessage());
            log.info("用户:" + user.getId() + "登录客户端:" + "" + "失败" + ex.getMessage());
            return result;
        } catch (IncorrectCredentialsException ex) {
            //错误凭证错误信息
            result.setSuccess(false).setMsg(ex.getMessage())
                    .getParam().put(SerConstant.ERROR_MSG, ex.getMessage());
            log.info("用户:" + user.getId() + "登录客户端:" + "" + "失败" + ex.getMessage());
            return result;
        } catch (Exception ex) {
            //其他异常错误信息
            result.setSuccess(false).setMsg(ex.getMessage())
                    .getParam().put(SerConstant.ERROR_MSG, ex.getMessage());
            log.info("用户:" + user.getId() + "登录客户端:" + "" + "失败" + ex.getMessage());
            return result;
        } finally {
            result.getParam().put(SerConstant.USERNAME, username);
            result.getParam().put(SerConstant.LOGIN_TYPE, loginType.toString());
        }
    }

    @Override
    public boolean retryLimit(String userId, boolean matches) {
        SSOUser user = userService.getUserById(userId);
        if (user == null) {
            log.error(userId + SerConstant.INVALID_USER_ID_DESCRIPTION);
            throw new IncorrectCredentialsException(SerConstant.INVALID_USER_ID_DESCRIPTION);
        }
        if (SerConstant.AccountState.禁用.equals(user.getStatus())) {
            log.error(userId + SerConstant.ACCOUNT_DISABLE_DESCRIPTION);
            throw new IncorrectCredentialsException(SerConstant.ACCOUNT_DISABLE_DESCRIPTION);
        }
        if (SerConstant.AccountState.删除.equals(user.getStatus())) {
            log.error(userId + SerConstant.ACCOUNT_DELETE_DESCRIPTION);
            throw new IncorrectCredentialsException(SerConstant.ACCOUNT_DELETE_DESCRIPTION);
        }
        int count = getLoginCount(userId);
        if (matches) {
            //清空重试次数
            removeLoginCount(userId);
            return true;
        }
        Map<String, String> params = new HashMap<>();
        if (count >= ERROR_COUNT) {
            String error = MessageFormat.format("{0},连续输错5次密码，账号锁定"
                    , SerConstant.INVALID_USER_PASSWORD_DESCRIPTION);
            log.error(userId + error);
            //规定时间内重试ERROR_COUNT次，抛出多次尝试异常
            throw new ExcessiveAttemptsException(error);
        }
        String error = MessageFormat.format("{0},连续出错{1}次,错误{2}次将被锁定"
                , SerConstant.INVALID_USER_PASSWORD_DESCRIPTION, count, ERROR_COUNT);
        log.error(userId + error);
        throw new IncorrectCredentialsException(error);
    }

    /**
     * 获取30分钟内登录次数
     *
     * @param userId
     * @return
     */
    public int getLoginCount(String userId) {
        RedisAtomicLong ral = new RedisAtomicLong(RedisPrefix.buildLoginCountKey(userId)
                , redisTemplate.getConnectionFactory());
        ral.incrementAndGet();
        //第一次设置允许错误的时间间隔
        if (ral.intValue() == 1) {
            ral.expire(ERROR_TIME_INTERVAL, TimeUnit.MINUTES);
        }
        return ral.intValue();
    }

    /**
     * 移除登录次数
     *
     * @param userId
     */
    public void removeLoginCount(String userId) {
        redisTemplate.delete(RedisPrefix.buildLoginCountKey(userId));
    }
}
