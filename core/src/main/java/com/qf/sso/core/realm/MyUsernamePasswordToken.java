package com.qf.sso.core.realm;

import com.qf.sso.core.common.SerConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * @author qiufeng
 * @date 2020/2/10 19:28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MyUsernamePasswordToken extends UsernamePasswordToken {
    private String account;
    private SerConstant.LoginType loginType = SerConstant.LoginType.密码;
    public MyUsernamePasswordToken(String username,String password){
        super(username, password);
    }
    public MyUsernamePasswordToken(String username,String password,boolean rememberMe){
        super(username, password, rememberMe);
    }
}
