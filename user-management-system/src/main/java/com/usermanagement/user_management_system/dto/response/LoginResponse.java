package com.usermanagement.user_management_system.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/20/2026
 */
@Data
@Builder
public class LoginResponse {

    private String accessToken;

    private String tokenType;

    private long expiresIn;

    private String refreshToken;

}