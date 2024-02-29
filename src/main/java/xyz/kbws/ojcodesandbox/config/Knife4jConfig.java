package xyz.kbws.ojcodesandbox.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author kbws
 * @date 2024/2/29
 * @description: Knife4j 接口文档配置
 */
@Configuration
@EnableSwagger2
public class Knife4jConfig {
    @Bean
    public Docket defaultApi2()
    {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(new ApiInfoBuilder().title("接口文档").description("oj_backend").version("1.0").build()).select()
                // 指定 Controller 扫描包路径
                .apis(RequestHandlerSelectors.basePackage("xyz.kbws.ojcodesandbox.controller")).paths(PathSelectors.any()).build();
    }
}
