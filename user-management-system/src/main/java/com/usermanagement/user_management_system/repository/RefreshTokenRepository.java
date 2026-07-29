package com.usermanagement.user_management_system.repository;

import com.usermanagement.user_management_system.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/22/2026
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(long userId);
    void deleteByUserId(long userId);
}
