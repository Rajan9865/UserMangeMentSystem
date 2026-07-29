package com.usermanagement.user_management_system.controller;

import com.usermanagement.user_management_system.auth.dto.RefreshTokenRequest;
import com.usermanagement.user_management_system.dto.request.LoginRequest;
import com.usermanagement.user_management_system.dto.response.LoginResponse;
import com.usermanagement.user_management_system.service.AuthServices;
import com.usermanagement.user_management_system.util.ApiResult;
import com.usermanagement.user_management_system.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/21/2026
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthServices authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResult<LoginResponse>> login(@Valid @RequestBody LoginRequest request
            , HttpServletRequest httpRequest) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ResponseUtil.success(response
                , "login successfully", HttpStatus.OK, httpRequest.getRequestURI()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResult<LoginResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest servletRequest) {
        LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(
                ResponseUtil.success(response,
                        "Token refreshed successfully",
                        HttpStatus.CREATED,
                        servletRequest.getRequestURI()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Void>> logout(
            HttpServletRequest servletRequest) {
        authService.logout();
        return ResponseEntity.ok(
                ResponseUtil.success(null,
                        "Logout successful",
                        HttpStatus.OK,
                        servletRequest.getRequestURI()));
    }
}
