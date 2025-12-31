package com.thiru.BookMyShow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI openAPI() {

                // 🔑 Bearer JWT
                SecurityScheme bearerAuth = new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT");

                // 🔐 Basic Auth
                SecurityScheme basicAuth = new SecurityScheme()
                                .name("basicAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic");

                return new OpenAPI()
                                .components(new Components()
                                                .addSecuritySchemes("bearerAuth", bearerAuth)
                                                .addSecuritySchemes("basicAuth", basicAuth));
        }
}
