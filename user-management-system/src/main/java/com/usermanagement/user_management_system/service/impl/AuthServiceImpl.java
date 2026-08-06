package com.usermanagement.user_management_system.service.impl;

import com.usermanagement.user_management_system.auth.dto.RefreshTokenRequest;
import com.usermanagement.user_management_system.dto.request.ChangePasswordRequest;
import com.usermanagement.user_management_system.dto.request.LoginRequest;
import com.usermanagement.user_management_system.dto.response.LoginResponse;
import com.usermanagement.user_management_system.entity.RefreshToken;
import com.usermanagement.user_management_system.entity.User;
import com.usermanagement.user_management_system.exception.InvalidPasswordException;
import com.usermanagement.user_management_system.repository.UserRepository;
import com.usermanagement.user_management_system.security.jwt.JwtProperties;
import com.usermanagement.user_management_system.security.jwt.JwtService;
import com.usermanagement.user_management_system.security.model.CustomUserDetails;
import com.usermanagement.user_management_system.service.AuthServices;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/21/2026
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthServices {

    private final AuthenticationManager authenticationManager;
    private final JwtService  jwtService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        System.out.println(jwtProperties.getSecret());
    }
    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),
                request.getPassword()));
CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtService.generateJwtToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser());

        return LoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .build();
    }

    @Override
        public LoginResponse refreshToken(
                RefreshTokenRequest request) {
        RefreshToken refreshToken =refreshTokenService.verifyToken(request.getRefreshToken());
        User user = refreshToken.getUser();
        CustomUserDetails userDetails =new CustomUserDetails(user);
        String accessToken =jwtService.generateJwtToken(userDetails);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .build();
    }

    @Override
    public void logout() {
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {return;}
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();
        refreshTokenService.deleteByUserId(userDetails.getUser().getId());
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        // 1. Get calling user from the security context (JWT already validated by filter)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // 2. Re-load the user from DB to get the latest persisted password hash
        User freshUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // 3. Verify the supplied current password against the stored BCrypt hash
        if (!passwordEncoder.matches(request.getCurrentPassword(), freshUser.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        // 4. Encode and persist the new password
        freshUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(freshUser);

        // 5. Invalidate all refresh tokens — forces re-login
        refreshTokenService.deleteByUserId(freshUser.getId());
    }
}
