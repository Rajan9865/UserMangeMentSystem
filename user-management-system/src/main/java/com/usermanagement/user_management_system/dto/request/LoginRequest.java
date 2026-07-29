package com.usermanagement.user_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/20/2026
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

}