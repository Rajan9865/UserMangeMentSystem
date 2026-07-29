package com.usermanagement.user_management_system.security.service;

import com.usermanagement.user_management_system.entity.User;
import com.usermanagement.user_management_system.exception.UserNotFoundException;
import com.usermanagement.user_management_system.repository.UserRepository;
import com.usermanagement.user_management_system.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/21/2026
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(
            String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->new UserNotFoundException("User not found : " + username));
        return new CustomUserDetails(user);
    }
}
