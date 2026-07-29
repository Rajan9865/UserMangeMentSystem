package com.usermanagement.user_management_system.service.impl;

import com.usermanagement.user_management_system.dto.UserRequestDto;
import com.usermanagement.user_management_system.dto.UserResponseDto;
import com.usermanagement.user_management_system.enums.Role;
import com.usermanagement.user_management_system.exception.InvalidUserException;
import com.usermanagement.user_management_system.exception.UserNotFoundException;
import com.usermanagement.user_management_system.repository.UserRepository;
import com.usermanagement.user_management_system.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/15/2026
 */
@Service
@Profile("memory")
public class CollectionUserService implements UserService {

    private final Map<Long, UserResponseDto> users =
            new ConcurrentHashMap<>();

    private final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        boolean userNameExists = users.values().stream().anyMatch(user -> user.getUsername().equalsIgnoreCase(request.getUsername()));
        if (userNameExists) {
            throw new InvalidUserException("Username already exists");
        }
        boolean emailExists = users.values().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(request.getEmail()));
        if (emailExists) {
            throw new InvalidUserException("Email already exists");
        }
        long id = counter.getAndIncrement();
        UserResponseDto response = UserResponseDto.builder().id(id)
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail()).build();
        users.put(id, response);
        return response;
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        UserResponseDto user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("User not found :"+id);
        }
        return user;
    }

    @Override
    public Page<UserResponseDto> getallUsers(Pageable pageable) {
        return null;
    }

//    @Override
//    public List<UserResponseDto> getAllUsers() {
//       return users.values().stream().toList();
//    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto request) {
        UserResponseDto existingUser = users.get(id);
        if (existingUser == null) {
            throw new UserNotFoundException("User not found with id : " + id);
        }
        existingUser.setUsername(request.getUsername());
        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setEmail(request.getEmail());
        users.put(id, existingUser);
        return existingUser;
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("User not found with id : " + id);
        }
        users.remove(id);
    }

    @Override
    public List<UserResponseDto> searchUsers(String username) {
        return List.of();
    }

    @Override
    public UserResponseDto updateUserRole(Long id, Role role) {
        return null;
    }
}
