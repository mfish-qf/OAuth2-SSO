package com.qf.sso.core.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qiufeng
 * @date 2020/2/17 16:23
 */
@ApiModel("错误返回")
@Data
public class ErrorResponseInfo {

    public ErrorResponseInfo(String code, String message) {
        super();
        setErrorCode(code);
        setErrorMsg(message);
    }

    @ApiModelProperty("错误编码")
    private String errorCode;

    @ApiModelProperty("错误描述")
    private String errorMsg;
}
