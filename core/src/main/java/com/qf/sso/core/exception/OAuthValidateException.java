package com.qf.sso.core.exception;

/**
 * @author qiufeng
 * @date 2020/2/17 16:32
 */
public class OAuthValidateException extends RuntimeException {
    public OAuthValidateException(String msg){
        super(msg);
    }
}
