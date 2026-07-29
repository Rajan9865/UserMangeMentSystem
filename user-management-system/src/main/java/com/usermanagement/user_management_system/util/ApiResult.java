package com.usermanagement.user_management_system.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/16/2026
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/*public class ApiResult<T> {
    private boolean success;
    private String message;
    private T data;
}*/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {

    private boolean success;

    private int status;

    private String message;

    private String errorCode;

    private T data;

    private Object errors;

    private String path;

    private LocalDateTime timestamp;
}