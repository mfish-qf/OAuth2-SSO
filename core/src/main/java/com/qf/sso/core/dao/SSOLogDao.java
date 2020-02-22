package com.qf.sso.core.dao;

import com.qf.sso.core.model.SSOLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qiufeng
 * @date 2020/2/22 11:36
 */
@Mapper
public interface SSOLogDao {
    int insert(SSOLog log);
}
