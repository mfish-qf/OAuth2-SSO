package com.qf.sso.core.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author qiufeng
 * @date 2020/2/10 15:51
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("swagger接口文档")
                .apiInfo(new ApiInfoBuilder().title("swagger接口文档")
                .contact(new Contact("qiufeng", "", "qiufeng9862@qq.com"))
                .version("1.0").build()).select().paths(PathSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*"))).build();
    }
}
