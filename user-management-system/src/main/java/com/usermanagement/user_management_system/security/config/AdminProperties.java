package com.usermanagement.user_management_system.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/22/2026
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.admin")
public class AdminProperties {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
}
