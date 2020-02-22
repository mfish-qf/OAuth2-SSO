package com.qf.sso.core.validator;

import com.qf.sso.core.common.CheckWithResult;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @date 2020/2/13 13:43
 */
public interface IBaseValidator<T> {
    CheckWithResult<T> validate(HttpServletRequest request, CheckWithResult<T> result);
}
