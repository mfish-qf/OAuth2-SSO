package com.qf.sso.appOAuth2.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiufeng
 * @date 2021/10/26 16:00
 */
@Configuration
@EnableConfigurationProperties(WeChatProperties.class)
public class WeChatConfig {
    private WeChatProperties weChatProperties;

    @Autowired
    public WeChatConfig(WeChatProperties weChatProperties) {
        this.weChatProperties = weChatProperties;
    }

    @Bean
    public WxMaService wxMaService() {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(weChatProperties.getAppId());
        config.setSecret(weChatProperties.getSecret());
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        return service;
    }
}