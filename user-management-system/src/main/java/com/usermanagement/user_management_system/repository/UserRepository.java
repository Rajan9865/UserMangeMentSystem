package com.usermanagement.user_management_system.repository;

import com.usermanagement.user_management_system.entity.User;
import com.usermanagement.user_management_system.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/15/2026
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByUsernameContainingIgnoreCase(String username);

    long countByRole(Role role);
}
