package com.qf.sso.appOAuth2.validator;


import com.qf.sso.appOAuth2.model.WeChatToken;
import com.qf.sso.appOAuth2.service.WeChatService;
import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.common.Utils;
import com.qf.sso.core.validator.IBaseValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author qiufeng
 * @date 2020/3/1 15:00
 */
@Component
public class WeChatTokenValidator implements IBaseValidator<WeChatToken> {
    @Resource
    WeChatService weChatService;

    public CheckWithResult<WeChatToken> validate(HttpServletRequest request) {
        return validate(request, null);
    }

    @Override
    public CheckWithResult<WeChatToken> validate(HttpServletRequest request, CheckWithResult<WeChatToken> result) {
        WeChatToken weChatToken;
        if (result == null || result.getResult() == null) {
            result = new CheckWithResult<>();
            String accessToken = Utils.getAccessToken(request);
            if (StringUtils.isEmpty(accessToken)) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            weChatToken = weChatService.getToken(accessToken);
            if (weChatToken == null) {
                return result.setSuccess(false).setMsg("错误:token不正确");
            }
            return result.setResult(weChatToken);
        }
        return result;
    }
}
