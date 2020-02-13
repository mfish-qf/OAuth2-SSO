package com.qf.sso.core.dao;

import com.qf.sso.core.model.SSOUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qiufeng
 * @date 2020/2/13 17:13
 */
@Mapper
public interface SSOUserDao {
    int update(SSOUser ssoUser);
}
