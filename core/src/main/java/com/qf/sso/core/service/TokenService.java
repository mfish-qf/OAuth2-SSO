package com.qf.sso.core.service;

/**
 * @author qiufeng
 * @date 2020/2/29 16:33
 */
public interface TokenService<T> {
    void setToken(T token);

    void delToken(String token);

    T getToken(String token);

    void setRefreshToken(T token);

    void updateRefreshToken(T token);

    T getRefreshToken(String token);

    void delRefreshToken(String token);
}
