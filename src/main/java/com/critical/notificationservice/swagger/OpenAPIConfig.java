package com.critical.notificationservice.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenAPIConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer oAuth Token";

    @Bean
    public OpenAPI myOpenAPI() {

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Notification Service Management API")
                .version("1.0")
                .description("This API exposes endpoints to manage the notifications.")
                .license(mitLicense);

        var oauthConfig = new Components()
                .addSecuritySchemes("spring_oauth", new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .description("Oauth2 flow")
                        .flows(new OAuthFlows()
                                .clientCredentials(new OAuthFlow()
                                        .tokenUrl("http://localhost:8080/realms/BookApplication/protocol/openid-connect/token")
                                        .scopes(new Scopes()
                                                .addString("read", "for read operations")
                                                .addString("write", "for write operations")
                                        ))));

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(oauthConfig)
                .security(Arrays.asList(new SecurityRequirement().addList("spring_oauth")))
                .info(info);
    }
}