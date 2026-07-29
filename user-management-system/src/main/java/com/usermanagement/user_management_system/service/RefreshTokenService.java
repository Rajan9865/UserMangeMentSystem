package com.usermanagement.user_management_system.service;

import com.usermanagement.user_management_system.entity.RefreshToken;
import com.usermanagement.user_management_system.entity.User;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/22/2026
 */
public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyToken(String token);
    void deleteByUserId(long userId);
}
