package com.qf.sso.core.validator;

import com.qf.sso.core.common.ApplicationContextProvider;
import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.model.AuthorizationCode;
import com.qf.sso.core.model.OAuthClient;
import com.qf.sso.core.model.RedisAccessToken;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qiufeng
 * @date 2020/2/13 14:41
 */
@Data
public abstract class MultipleValidator {
    @Autowired
    ApplicationContextProvider applicationContextProvider;

    List<Class<? extends IBaseValidator<OAuthClient>>> validateClientList = new ArrayList<>();


    /**
     * 客户端参数相关多个校验
     *
     * @param request
     * @param result
     * @return
     */
    public CheckWithResult<?> validateClient(HttpServletRequest request, CheckWithResult<OAuthClient> result) {
        return validate(request, result, validateClientList);
    }

    public <T> CheckWithResult<?> validate(HttpServletRequest request, CheckWithResult<T> result, List<Class<? extends IBaseValidator<T>>> list) {
        for (Class<? extends IBaseValidator<T>> validator : list) {
            result = applicationContextProvider.getBean(validator)
                    .validate(request, result);
            if (!result.isSuccess()) {
                return result;
            }
        }
        return result;
    }
}
