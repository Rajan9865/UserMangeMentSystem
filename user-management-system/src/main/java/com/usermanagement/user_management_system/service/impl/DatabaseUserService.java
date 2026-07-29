package com.usermanagement.user_management_system.service.impl;

import com.usermanagement.user_management_system.dto.UserRequestDto;
import com.usermanagement.user_management_system.dto.UserResponseDto;
import com.usermanagement.user_management_system.entity.User;
import com.usermanagement.user_management_system.enums.Role;
import com.usermanagement.user_management_system.exception.InvalidUserException;
import com.usermanagement.user_management_system.exception.UserNotFoundException;
import com.usermanagement.user_management_system.repository.UserRepository;
import com.usermanagement.user_management_system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public DatabaseUserService(UserRepository repository,
                               ModelMapper mapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
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
        return mapper.map(updated, UserResponseDto.class);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user. Id={}", id);
        repository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id : " + id));
        log.info("User deleted successfully. Id={}", id);
        repository.deleteById(id);
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
        if (user.getRole() == Role.ROLE_ADMIN&&role == Role.ROLE_USER) {
            long adminCount = repository.countByRole(Role.ROLE_ADMIN);
            if (adminCount ==1) {
                throw new InvalidUserException("cannot demote the last administrator");
            }
        }
        user.setRole(role);
        User updatedUser = repository.save(user);
        log.info("User role  updated successfully. Id={}", updatedUser.getId());
        return mapper.map(updatedUser, UserResponseDto.class);
    }
}
