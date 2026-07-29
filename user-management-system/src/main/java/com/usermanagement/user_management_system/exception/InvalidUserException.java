package com.usermanagement.user_management_system.exception;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/16/2026
 */
public class InvalidUserException extends RuntimeException
{
    public InvalidUserException(String message)
    {
        super(message);
    }
}
