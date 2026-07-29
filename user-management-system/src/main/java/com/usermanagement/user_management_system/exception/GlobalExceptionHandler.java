package com.usermanagement.user_management_system.exception;

import com.usermanagement.user_management_system.common.response.ErrorResponse;
import com.usermanagement.user_management_system.common.response.constants.ErrorCodes;
import com.usermanagement.user_management_system.util.ApiResult;
import com.usermanagement.user_management_system.util.ResponseUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/16/2026
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*    @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ApiResult<Object>> handleUserNotFound(UserNotFoundException ex) {
            log.error("UserNotFoundException : {}", ex.getMessage());
            ApiResult<Object> response = ApiResult.builder()
                    .success(false)
                    .message(ex.getMessage())
                    .data(null)
                    .build();

            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }*/
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .status(HttpStatus.NOT_FOUND.value())
                .errorCode(ErrorCodes.USER_NOT_FOUND)
                .message(e.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

   /* @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ApiResult<Object>> handleInvalidUser(InvalidUserException ex) {

        ApiResult<Object> response = ApiResult.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }*/

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserException(InvalidUserException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder().success(false)
                .status(HttpStatus.CONFLICT.value())
                .errorCode(ErrorCodes.USER_ALREADY_EXISTS)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /*@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validationsErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> validationsErrors.put(error.getField(), error.getDefaultMessage()));
        ErrorResponse response = ErrorResponse.builder().success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ErrorCodes.VALIDATION_ERROR)
                .message("Validation Failed")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .errors(validationsErrors)
                .build();
//    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        return ResponseEntity.badRequest().body(response);
    }

    /* @ExceptionHandler(Exception.class)
     public ResponseEntity<ApiResult<Object>> handleException(Exception ex) {
         log.error("Unexpected Exception", ex);
         ApiResult<Object> response = ApiResult.builder()
                 .success(false)
                 .message(ex.getMessage())
                 .data(null)
                 .build();

         return new ResponseEntity<>(response,
                 HttpStatus.INTERNAL_SERVER_ERROR);
     }*/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        log.error("unexpected error", ex);
        ErrorResponse response = ErrorResponse.builder().success(false)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode(ErrorCodes.INTERNAL_SERVER_ERROR)
                .message("something went wrong")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder().success(false)
                .status(HttpStatus.UNAUTHORIZED.value())
                .errorCode("bad credentials")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder().success(false)
                .status(HttpStatus.FORBIDDEN.value())
                .errorCode("access denied")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder().success(false)
                .status(HttpStatus.UNAUTHORIZED.value())
                .errorCode("invalid_refresh_token")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}