package com.usermanagement.user_management_system.service.impl;

import com.usermanagement.user_management_system.entity.RefreshToken;
import com.usermanagement.user_management_system.entity.User;
import com.usermanagement.user_management_system.repository.RefreshTokenRepository;
import com.usermanagement.user_management_system.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/22/2026
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());
        RefreshToken refreshToken = RefreshToken.builder().user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() ->
                new RuntimeException("Refresh token not found!"));
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired!");
        }
        return refreshToken;
    }

    @Override
    public void deleteByUserId(long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
