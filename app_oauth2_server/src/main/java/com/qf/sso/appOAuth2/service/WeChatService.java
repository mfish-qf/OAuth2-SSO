package com.qf.sso.appOAuth2.service;

import com.qf.sso.appOAuth2.model.WeChatToken;
import com.qf.sso.core.common.CheckWithResult;
import com.qf.sso.core.model.AccessToken;
import com.qf.sso.core.service.TokenService;

import java.util.Map;

/**
 * @author qiufeng
 * @date 2020/2/28 17:32
 */
public interface WeChatService extends TokenService<WeChatToken> {

    /**
     * 通过openid获取用户id,检查微信绑定状态
     *
     * @param openId
     * @return
     */
    String getUserIdByOpenId(String openId);

    /**
     * 微信用户绑定
     *
     * @param openId
     * @param userId
     * @return
     */
    boolean bindWeChat(String openId, String userId);

    /**
     * 微信用户绑定
     *
     * @param openId
     * @param userId
     * @param nickname
     * @return
     */
    boolean bindWeChat(String openId, String userId,String nickname);

    /**
     * 构建微信token
     *
     * @param openId
     * @param sessionKey
     * @return
     */
    WeChatToken buildWeChatToken(String openId, String sessionKey, String userId);

    /**
     * 将微信token转化成通用accessToken
     *
     * @param weChatToken
     * @return
     */
    AccessToken convertToken(WeChatToken weChatToken);
}
