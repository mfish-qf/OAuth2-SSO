package com.qf.sso.core.advice;

import com.qf.sso.core.common.ErrorResponseInfo;
import com.qf.sso.core.exception.OAuthValidateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;

/**
 * @author qiufeng
 * @date 2020/2/17 16:22
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {
    @ExceptionHandler(OAuthValidateException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseInfo exception(OAuthValidateException exception) {
        log.error("401校验异常", exception);
        return new ErrorResponseInfo(HttpStatus.UNAUTHORIZED.value() + "", exception.getMessage());

    }

    @ExceptionHandler({ IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseInfo badRequestException(IllegalArgumentException exception) {
        log.error("400异常", exception);
        return new ErrorResponseInfo(HttpStatus.BAD_REQUEST.value() + "", exception.getMessage());
    }

    @ExceptionHandler({ AccessDeniedException.class })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseInfo badRequestException(AccessDeniedException exception) {
        log.error("403异常", exception);
        return new ErrorResponseInfo(HttpStatus.FORBIDDEN.value() + "", exception.getMessage());
    }

    @ExceptionHandler({ MissingServletRequestParameterException.class, HttpMessageNotReadableException.class,
            UnsatisfiedServletRequestParameterException.class, MethodArgumentTypeMismatchException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseInfo badRequestException(Exception exception) {
        log.error("400异常", exception);
        return new ErrorResponseInfo(HttpStatus.BAD_REQUEST.value() + "", exception.getMessage());
    }
}
