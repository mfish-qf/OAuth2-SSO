package com.qf.sso.core.validator;

import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.common.Utils;
import com.qf.sso.core.model.RedisAccessToken;
import com.qf.sso.core.service.OAuth2Service;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.OAuth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @date 2020/2/18 18:53
 */
public abstract class AbstractRefreshTokenValidator implements IBaseValidator<RedisAccessToken> {
    @Resource
    OAuth2Service oAuth2Service;

    public CheckWithResult<RedisAccessToken> getRefreshToken(HttpServletRequest request, CheckWithResult<RedisAccessToken> result) {
        RedisAccessToken token;
        if (result == null || result.getResult() == null) {
            result = new CheckWithResult<>();
            String refreshToken = request.getParameter(OAuth.OAUTH_REFRESH_TOKEN);
            if (StringUtils.isEmpty(refreshToken)) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            token = oAuth2Service.getRefreshToken(refreshToken);
            if (token == null) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            return result.setResult(token);
        }
        return result;
    }
}
