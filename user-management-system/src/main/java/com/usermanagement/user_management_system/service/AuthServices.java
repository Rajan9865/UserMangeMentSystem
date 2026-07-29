package com.usermanagement.user_management_system.service;

import com.usermanagement.user_management_system.auth.dto.RefreshTokenRequest;
import com.usermanagement.user_management_system.dto.request.LoginRequest;
import com.usermanagement.user_management_system.dto.response.LoginResponse;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/21/2026
 */
public interface AuthServices{
    LoginResponse login(LoginRequest request);
    LoginResponse refreshToken(RefreshTokenRequest request);
    void logout();
}
