package io.sf.admin.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * OpenAPI 配置类
 * 配置Swagger UI和API文档
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("用户认证系统 Admin")
                        .version("1.0.0")
                        .description("基于Spring Security和JWT的用户认证系统API文档")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                )
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .name("JWT")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT认证令牌，格式: Bearer {token}"))
                );
    }

    @Bean
    public OpenApiCustomizer globalContentTypeHeader() {
        return openApi -> {
            if (openApi.getPaths() == null) return;
            openApi.getPaths().values().forEach(pathItem ->
                    pathItem.readOperations().forEach(operation -> {
                        boolean exists = operation.getParameters() != null && operation.getParameters().stream()
                                .anyMatch(p -> "Content-Type".equalsIgnoreCase(p.getName()) && "header".equalsIgnoreCase(p.getIn()));
                        if (!exists) {
                            operation.addParametersItem(new Parameter()
                                    .name("Content-Type")
                                    .in("header")
                                    .description("请求内容类型")
                                    .required(true)
                                    .schema(new StringSchema()._default("application/json")));
                        }
                    })
            );
        };
    }
}