package com.usermanagement.user_management_system.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Rajan kumar
 * @version 1.0
 * Practise_Project
 * @since 7/22/2026
 */
@Data
public class RefreshTokenRequest {
    @NotBlank
    private String refreshToken;
}
