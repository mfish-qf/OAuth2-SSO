package com.qf.sso.core.validator;

import com.qf.sso.core.common.CheckWithResult;

/**
 * @author qiufeng
 * @date 2020/2/13 13:43
 */
public interface IBaseValidator<T,P> {
    CheckWithResult<T> validate(P p);
}
