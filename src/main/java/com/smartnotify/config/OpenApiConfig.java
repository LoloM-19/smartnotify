package com.smartnotify.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartNotifyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartNotify API")
                        .description("A rate-limited notification microservice built with Spring Boot, Redis and PostgreSQL")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Lolo Mphahlele")
                                .email("lolomphahlele6@gmail.com")));
    }
}