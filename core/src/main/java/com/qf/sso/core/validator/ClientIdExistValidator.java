package com.qf.sso.core.validator;

import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.model.OAuthClient;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @date 2020/2/13 13:58
 */
@Component
public class ClientIdExistValidator extends AbstractClientValidator {

    @Override
    public CheckWithResult<OAuthClient> validate(HttpServletRequest request, CheckWithResult<OAuthClient> result) {
        return getOAuthClient(request, result);
    }
}
