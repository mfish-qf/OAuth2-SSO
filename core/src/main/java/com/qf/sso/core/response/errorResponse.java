package com.qf.sso.core.response;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

/**
 * @author qiufeng
 * @date 2020/2/13 14:01
 */
@Slf4j
public class errorResponse {
    public static ResponseEntity<Object> unauthorizedResponse(String error, String desc) throws OAuthSystemException {
        OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                .setError(error).setErrorDescription(desc)
                .buildJSONMessage();
        log.info(desc);
        return new ResponseEntity<>(JSON.parse(response.getBody())
                , HttpStatus.UNAUTHORIZED);
    }
}
