package com.usermanagement.user_management_system.util;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/20/2026
 */
public class ResponseUtil {
    private ResponseUtil() {
    }

    public static <T> ApiResult<T> success(
            T data,
            String message,
            HttpStatus status,
            String path) {

        return ApiResult.<T>builder()
                .success(true)
                .status(status.value())
                .message(message)
                .data(data)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResult<Object> error(
            String message,
            String errorCode,
            Object errors,
            HttpStatus status,
            String path) {

        return ApiResult.builder()
                .success(false)
                .status(status.value())
                .message(message)
                .errorCode(errorCode)
                .errors(errors)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
