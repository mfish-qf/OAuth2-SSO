package com.qf.sso.appOAuth2.model;

import com.qf.sso.core.model.AccessToken;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qiufeng
 * @date 2020/2/29 16:20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("缓存中的微信token")
public class WeChatToken extends AccessToken {
    @ApiModelProperty("微信openid")
    private String openid;
    @ApiModelProperty("微信返回session参数")
    private String session_key;
    @ApiModelProperty("用户id")
    private String userId;
    @ApiModelProperty("用户账号")
    private String account;
}
