package com.usermanagement.user_management_system.exception;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/23/2026
 */
public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
