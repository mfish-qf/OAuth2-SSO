package com.qf.sso.webOAuth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author qiufeng
 * @date 2020/2/10 16:00
 */

@SpringBootApplication
@ComponentScan(basePackages = {"com.qf.sso.webOAuth2", "com.qf.sso.core"})
public class WebOAuth2Application {
    public static void main(String[] args) {
        SpringApplication.run(WebOAuth2Application.class, args);
    }
}
