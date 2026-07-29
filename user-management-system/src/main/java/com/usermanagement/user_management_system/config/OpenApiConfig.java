package com.usermanagement.user_management_system.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/17/2026
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI userManagementOpenAPI() {
return new OpenAPI().info(new Info().title("User Management System")
.description("REST APIs for User Management System")
.version("1.0").contact(new Contact()
                        .name("Rajan kumar")
                .email("rajan06166@gmail.com")
                .url("https://www.linkedin.com/in/rajan-kumar-78800/"))
        .license(new License()
                .name("Apace 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0")))
        .components(new Components()
                .addSecuritySchemes("basicAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")))
        .externalDocs(new ExternalDocumentation()
                .description("Project Documentation")
                .url("https://github.com/Rajan9865/Practise_Project"));
    }
}
