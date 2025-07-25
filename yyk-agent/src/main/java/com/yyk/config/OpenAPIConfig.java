package com.yyk.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("亚克AI智能体(yyk-agent-pg)")
                        .description("智能体API文档，提供接口说明和调试功能")
                        .version("1.0")
                        .contact(new Contact()
                                .name("YYK")
                                .url("http://www.isyyk.top/")
                                .email("yyk020906@163.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
}
