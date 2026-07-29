package com.usermanagement.user_management_system.service;

import com.usermanagement.user_management_system.dto.UserRequestDto;
import com.usermanagement.user_management_system.dto.UserResponseDto;
import com.usermanagement.user_management_system.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/15/2026
 */
public interface UserService {
    UserResponseDto createUser(UserRequestDto request);

    UserResponseDto getUserById(Long id);

//    List<UserResponseDto> getAllUsers();
        Page<UserResponseDto> getallUsers(Pageable pageable);

    UserResponseDto updateUser(Long id, UserRequestDto request);

    void deleteUser(Long id);

    List<UserResponseDto> searchUsers(String username);

    UserResponseDto updateUserRole(Long id, Role role);
}
