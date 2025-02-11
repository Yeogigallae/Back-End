package com.umc.yeogi_gal_lae.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        SecurityScheme securityScheme = new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        Components components = new Components()
                .addSecuritySchemes(jwt, securityScheme);

        return new OpenAPI()
                .info(apiInfo())
                // Swagger UI에서 "Authorize" 누르면 JWT 헤더 적용
//                .servers(Collections.singletonList(new Server().url("https://api.yeogi.my").description("Production Server")))
//                .servers(List.of(new Server().url("https://api.yeogi.my/api").description("Production Server")))
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    private Info apiInfo() {
        return new Info()
                .title("UMC 7th Team 여기갈래")
                .description("여기갈래 팀 Swagger 입니다.")
                .version("1.0.0");
    }
}