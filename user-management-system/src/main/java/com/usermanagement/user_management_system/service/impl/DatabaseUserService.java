package com.usermanagement.user_management_system.service.impl;

import com.usermanagement.user_management_system.dto.UserRequestDto;
import com.usermanagement.user_management_system.dto.UserResponseDto;
import com.usermanagement.user_management_system.dto.request.ChangePasswordRequest;
import com.usermanagement.user_management_system.dto.request.UpdateProfileRequest;
import com.usermanagement.user_management_system.entity.User;
import com.usermanagement.user_management_system.enums.AuditAction;
import com.usermanagement.user_management_system.enums.Role;
import com.usermanagement.user_management_system.exception.InvalidPasswordException;
import com.usermanagement.user_management_system.exception.InvalidUserException;
import com.usermanagement.user_management_system.exception.UserNotFoundException;
import com.usermanagement.user_management_system.repository.UserRepository;
import com.usermanagement.user_management_system.service.AuditLogService;
import com.usermanagement.user_management_system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/15/2026
 */
@Slf4j
@Service
@Profile("db")
public class DatabaseUserService implements UserService {

    private final UserRepository repository;
    private final ModelMapper mapper;

    //    private final PasswordEncoder passwordEncoder;
    private final PasswordEncoder passwordEncoder;

    private final AuditLogService auditLogService;

    public DatabaseUserService(UserRepository repository,
                               ModelMapper mapper,
                               PasswordEncoder passwordEncoder,
                               AuditLogService auditLogService) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    private String currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.getName() != null) ? auth.getName() : "system";
    }

    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        log.info("Create user request received. Username={}", request.getUsername());
        if (repository.existsByUsername(request.getUsername())) {
            log.warn("Duplicate username found: {}", request.getUsername());
            throw new InvalidUserException("Username already exists.");
        }
        if (repository.existsByEmail(request.getEmail())) {
            log.warn("Duplicate email found: {}", request.getEmail());
            throw new InvalidUserException("Email already exists.");
        }
        User user = mapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        User savedUser = repository.save(user);
        log.info("User created successfully. UserId={}", savedUser.getId());
        auditLogService.log(AuditAction.USER_CREATED, savedUser.getId(), savedUser.getUsername(), currentUser(), "User account created");
        return mapper.map(savedUser, UserResponseDto.class);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        log.info("Fetching user with id={}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found. Id={}", id);
                    return new UserNotFoundException("User not found with id : " + id);
                });
        log.info("User fetched successfully. Id={}", id);
        return mapper.map(user, UserResponseDto.class);
    }

//    @Override
//    public List<UserResponseDto> getAllUsers() {
//        return repository.findAll().stream().map(
//                user -> mapper.map(user, UserResponseDto.class)
//        ).toList();
//    }

    @Override
    public Page<UserResponseDto> getallUsers(Pageable pageable) {
        Page<User> all = repository.findAll(pageable);
        return all.map(user -> mapper.map(user, UserResponseDto.class));
    }


    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto request) {
        log.info("Updating user. Id={}", id);
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id : " + id));
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        User updated = repository.save(user);
        log.info("User updated successfully. Id={}", updated.getId());
        auditLogService.log(AuditAction.USER_UPDATED, updated.getId(), updated.getUsername(), currentUser(), "Profile information updated");
        return mapper.map(updated, UserResponseDto.class);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user. Id={}", id);
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id : " + id));
        log.info("User deleted successfully. Id={}", id);
        repository.deleteById(id);
        auditLogService.log(AuditAction.USER_DELETED, id, user.getUsername(), currentUser(), "User account deleted");
    }

    @Override
    public List<UserResponseDto> searchUsers(String username) {
        return repository.findByUsernameContainingIgnoreCase(username)
                .stream().map(user -> mapper.map(user, UserResponseDto.class))
                .toList();
    }

    @Override
    public UserResponseDto updateUserRole(Long id, Role role) {
        log.info("Updating user role with id={} to {}", id, role);
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id : " + id));
        //prevent demoting the last admin
        if (user.getRole() == Role.ROLE_ADMIN && role == Role.ROLE_USER) {
            long adminCount = repository.countByRole(Role.ROLE_ADMIN);
            if (adminCount == 1) {
                throw new InvalidUserException("cannot demote the last administrator");
            }
        }
        user.setRole(role);
        User updatedUser = repository.save(user);
        log.info("User role updated successfully. Id={}", updatedUser.getId());
        auditLogService.log(AuditAction.USER_ROLE_CHANGED, updatedUser.getId(), updatedUser.getUsername(), currentUser(), "Role changed to " + role.name());
        return mapper.map(updatedUser, UserResponseDto.class);
    }

    @Override
    public void changePassword(Long id, ChangePasswordRequest request) {
        log.info("Changing password for user id={}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id : " + id));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("Incorrect current password for user id={}", id);
            throw new InvalidPasswordException("Current password is incorrect.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidPasswordException("New password must differ from the current password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
        log.info("Password changed successfully for user id={}", id);
        auditLogService.log(AuditAction.PASSWORD_CHANGED, id, user.getUsername(), currentUser(), "Password updated");
    }

    @Override
    public UserResponseDto updateProfile(Long id, UpdateProfileRequest request) {
        log.info("Updating profile for user id={}", id);
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id : " + id));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        User updated = repository.save(user);
        log.info("Profile updated successfully for user id={}", updated.getId());
        auditLogService.log(AuditAction.PROFILE_UPDATED, updated.getId(), updated.getUsername(), currentUser(), "Profile details updated");
        return mapper.map(updated, UserResponseDto.class);
    }
}
