package com.qf.sso.core.service.impl;

import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.common.PasswordHelper;
import com.qf.sso.core.common.SerConstant;
import com.qf.sso.core.dao.SSOUserDao;
import com.qf.sso.core.model.SSOUser;
import com.qf.sso.core.model.redis.UserCipher;
import com.qf.sso.core.service.UserService;
import org.apache.shiro.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author qiufeng
 * @date 2020/2/13 16:51
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private PasswordHelper passwordHelper;
    @Resource(name = "redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    SSOUserDao ssoUserDao;

    /**
     * 修改用户密码
     *
     * @param userId
     * @param newPassword
     * @return
     */
    @Override
    public CheckWithResult<UserCipher> changePassword(String userId, String newPassword) {
        CheckWithResult<UserCipher> result = verifyPassword(newPassword);
        if (!result.isSuccess()) {
            return result;
        }
        UserCipher pwd = (UserCipher) redisTemplate.opsForValue().get(SerConstant.USER_PASSWORD + userId);
        if (pwd == null) {
            pwd = new UserCipher();
        }

        pwd.setOldPassword(setOldPwd(pwd.getOldPassword(), pwd.getPassword()));
        pwd.setPassword(passwordHelper.encryptPassword(userId, newPassword, pwd.getSalt()));
        if (pwd.getOldPassword().indexOf(pwd.getPassword()) >= 0) {
            return result.setSuccess(false).setMsg("密码5次内不得循环使用");
        }
        pwd.setModifyDate(String.valueOf(System.currentTimeMillis()));
        redisTemplate.opsForValue().set(SerConstant.USER_PASSWORD + userId, pwd);
        return result.setSuccess(true).setMsg("修改成功").setResult(pwd);
    }

    @Override
    public CheckWithResult<SSOUser> update(SSOUser user) {
        CheckWithResult<SSOUser> result = new CheckWithResult<SSOUser>().setResult(user);
        int res = ssoUserDao.update(user);
        if (res > 0) {
            return result;
        }
        return result.setSuccess(false).setMsg("未找到更新数据");
    }

    /**
     * 密码校验 密码长度必须8~16位
     * 密码必须由数字、字母、特殊字符组合
     *
     * @param password
     * @return
     */
    public CheckWithResult<UserCipher> verifyPassword(String password) {
        CheckWithResult<UserCipher> result = new CheckWithResult<>();
        if (password.length() < 8 || password.length() > 16) {
            return result.setSuccess(false).setMsg("密码长度必须8~16位");
        }
        if (!password.matches("(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{8,16}")) {
            return result.setSuccess(false).setMsg("密码必须由数字、字母、特殊字符组合");
        }
        return result;
    }

    /**
     * 设置旧密码 将最后一次密码添加到最近密码列表中，密码逗号隔开
     * 只存储最近5次密码
     *
     * @param oldPwd
     * @param pwd
     * @return
     */
    private String setOldPwd(String oldPwd, String pwd) {
        String[] pwds = "".equals(oldPwd) ? new String[0] : oldPwd.split(",");
        List<String> list = Arrays.asList(pwds);
        if (list.size() >= 5) {
            list.remove(0);
        }
        if (!"".equals(pwd)) {
            list.add(pwd);
        }
        return StringUtils.join(list.iterator(), ",");
    }
}
