package com.qf.sso.appOAuth2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author qiufeng
 * @date 2020/5/15 15:58
 */
@Data
@Configuration
@PropertySource(value = "classpath:wechat.properties", encoding = "gbk")
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WeChatProperties {
    /**
     * 设置微信小程序的appid
     */
    private String appId = "****";
    /**
     * 设置微信小程序的Secret
     */
    private String secret = "****";

}
