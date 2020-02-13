package com.qf.sso.core.model.redis;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author qiufeng
 * @date 2020/2/13 16:56
 */
@Data
@ApiModel("用户密码redis临时对象")
public class UserCipher {
    private String userId;
    private String password;
    private String oldPassword;
    private String salt;
    private String modifyDate;
}
