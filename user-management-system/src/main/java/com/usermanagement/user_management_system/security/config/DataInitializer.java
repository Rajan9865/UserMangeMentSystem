package com.usermanagement.user_management_system.security.config;

import com.usermanagement.user_management_system.entity.User;
import com.usermanagement.user_management_system.enums.Role;
import com.usermanagement.user_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/22/2026
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    @Bean
    CommandLineRunner init() {
        return args -> {

            if (!repository.existsByUsername(adminProperties.getUsername())) {

                User admin = new User();

                admin.setUsername(adminProperties.getUsername());
                admin.setFirstName(adminProperties.getFirstName());
                admin.setLastName(adminProperties.getLastName());
                admin.setEmail(adminProperties.getEmail());
                admin.setPassword(
                        passwordEncoder.encode(adminProperties.getPassword())
                );
                admin.setRole(Role.valueOf(adminProperties.getRole()));

                repository.save(admin);
            }
        };
    }
}