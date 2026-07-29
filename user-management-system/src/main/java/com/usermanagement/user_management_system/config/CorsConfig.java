package com.usermanagement.user_management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/28/2026
 *
 * CORS Configuration — allows Angular frontend (localhost:4200)
 * to communicate with the Spring Boot backend (localhost:8080).
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Allow Angular dev server origin
        config.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:3000"   // in case any other frontend
        ));

        // ✅ Allow all standard HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // ✅ Allow all headers (including Authorization for JWT)
        config.setAllowedHeaders(List.of("*"));

        // ✅ Allow cookies / Authorization header to be sent
        config.setAllowCredentials(true);

        // ✅ Cache preflight response for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
