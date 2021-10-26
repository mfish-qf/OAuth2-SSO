package com.qf.sso.core.dao;

import com.qf.sso.core.model.SSOUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author qiufeng
 * @date 2020/2/13 17:13
 */
@Mapper
public interface SSOUserDao {
    /**
     * 插入用户信息
     * @param userInfo
     * @return
     */
    int insert(SSOUser userInfo);
    /**
     * 更新用户信息
     *
     * @param ssoUser
     * @return
     */
    int update(SSOUser ssoUser);

    /**
     * 根据账号查询用户信息 账号为account,phone,email,userId任意一种
     *
     * @param account
     * @return
     */
    SSOUser getUserByAccount(@Param("account") String account);

    /**
     * 根据用户ID查询用户
     *
     * @param id
     * @return
     */
    @Select("select * from sso_user where id = #{id}")
    SSOUser getUserById(String id);

    /**
     * 根据微信openId获取用户id
     * @param openid
     * @return
     */
    @Select("select id from sso_user where openid=#{openId}")
    String getUserIdByOpenId(String openid);
}
