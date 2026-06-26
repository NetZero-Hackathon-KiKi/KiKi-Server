package com.netzero.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // 1. 보안 스키마 이름 지정
        String jwtSchemeName = "jwtAuth";

        // 2. API 요청 헤더에 토큰을 포함하도록 요구사항 정의
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // 3. SecurityScheme(인증 방식)을 'Bearer' 및 'JWT'로 설정
        Components components = new Components()
            .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT"));


        return new OpenAPI()
            .info(new Info()
                .title("NetZero API")
                .description("대학생 탄소중립 챌린지 앱 - NetZero REST API 문서")
                .version("v1.0.0"))
            .addSecurityItem(securityRequirement)
            .components(components);
    }
}