package com.qf.sso.core.validator;

import com.qf.sso.core.common.CheckWithResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author qiufeng
 * @date 2020/2/13 13:58
 */
@Component
public class ClientIdValidator implements IBaseValidator<CheckWithResult<ResponseEntity<Object>>,String> {

    @Override
    public CheckWithResult<CheckWithResult<ResponseEntity<Object>>> validate(String s) {
        return null;
    }
}
