package com.kopo.hanabank.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("하나은행 API")
                        .description("하나은행 서버 - 적금, 투자, 대출 관리 API")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("박지민")
                                .email("jimin1299@naver.com")));
    }
}












