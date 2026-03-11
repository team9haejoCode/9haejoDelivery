package com.sparta._9haejodelivery.config;

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
    // Swagger UI에서 JWT 토큰 인증 버튼(Authorize)이 보이게 하려면 SwaggerConfig 설정
    String jwtSchemeName = "jwtAuth";
    SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
    Components components = new Components().addSecuritySchemes(jwtSchemeName,
                                                                new SecurityScheme().name(jwtSchemeName)
                                                                                    .type(SecurityScheme.Type.HTTP)
                                                                                    .scheme("bearer")
                                                                                    .bearerFormat("JWT"));
    return new OpenAPI().addSecurityItem(securityRequirement).components(components).info(apiInfo());
  }

  private Info apiInfo() {
    return new Info().title("9해줘배달팀 api 명세서").version("1.0.0");
  }
}