package com.qf.sso.core.validator;

import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.model.AuthorizationCode;
import com.qf.sso.core.service.OAuth2Service;
import org.apache.oltu.oauth2.common.OAuth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @date 2020/2/17 15:39
 */
public abstract class AbstractCodeValidator implements IBaseValidator<AuthorizationCode> {
    @Resource
    OAuth2Service oAuth2Service;

    /**
     * 获取authCode信息
     *
     * @param request
     * @param result
     * @return
     */
    public CheckWithResult<AuthorizationCode> getAuthCode(HttpServletRequest request, CheckWithResult<AuthorizationCode> result) {
        AuthorizationCode authCode;
        if (result == null || result.getResult() == null) {
            result = new CheckWithResult<>();
            String code = request.getParameter(OAuth.OAUTH_CODE);
            authCode = oAuth2Service.getCode(code);
            if (authCode == null) {
                return result.setSuccess(false).setMsg("错误:code错误或失效!");
            }
            result.setResult(authCode);
        }
        return result;
    }
}
