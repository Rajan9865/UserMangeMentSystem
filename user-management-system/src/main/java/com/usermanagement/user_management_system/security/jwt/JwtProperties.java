package com.usermanagement.user_management_system.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/20/2026
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * Secret Key
     */
    private String secret;
    /**
     * Expiration Time
     */
    private long expiration;
}