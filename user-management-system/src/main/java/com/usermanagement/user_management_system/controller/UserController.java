package com.usermanagement.user_management_system.controller;

import com.usermanagement.user_management_system.dto.UserRequestDto;
import com.usermanagement.user_management_system.dto.UserResponseDto;
import com.usermanagement.user_management_system.dto.request.ChangePasswordRequest;
import com.usermanagement.user_management_system.dto.request.UpdateProfileRequest;
import com.usermanagement.user_management_system.enums.Role;
import com.usermanagement.user_management_system.service.UserService;
import com.usermanagement.user_management_system.util.ApiResult;
import com.usermanagement.user_management_system.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/16/2026
 */
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User API",description = "Operations related to User Management")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Create User
     */
    @PostMapping
    @Operation( summary = "Create User",description = "Creates a new user in the system.")
    @ApiResponses({@ApiResponse( responseCode = "201",description = "User created successfullyhhh"),
            @ApiResponse(responseCode = "400",description = "Validation failed"),
            @ApiResponse(responseCode = "409",description = "Username or email already exists")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResult<UserResponseDto>> createUser(
            @Valid @RequestBody UserRequestDto request,
            HttpServletRequest httpRequest) {
        UserResponseDto response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        response,
                        "User created successfully.",
                        HttpStatus.CREATED,
                        httpRequest.getRequestURI()));
    }

    /**
     * Get User By Id
     */

    @Operation(summary = "Get User By Id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public ResponseEntity<ApiResult<UserResponseDto>> getUserById(
            @PathVariable Long id, HttpServletRequest request) {
        UserResponseDto response = userService.getUserById(id);
        return ResponseEntity.ok(
                ResponseUtil.success(
                        response,
                        "User fetched successfully.",
                        HttpStatus.OK,
                        request.getRequestURI()));
    }

    /**
     * Get All Users
     */
//    @Operation(summary = "Get All Users")
//    @GetMapping
//    public ResponseEntity<ApiResult<List<UserResponseDto>>> getAllUsers() {
//        List<UserResponseDto> response = userService.getAllUsers();
//        ApiResult<List<UserResponseDto>> apiResponse =
//                ApiResult.<List<UserResponseDto>>builder()
//                        .success(true)
//                        .message("Users fetched successfully.")
//                        .data(response)
//                        .build();
//        return ResponseEntity.ok(apiResponse);
//    }
    @Operation(summary = "get all users")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR','USER')")
    public ResponseEntity<ApiResult<Page<UserResponseDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Get all users by page and size Rajan testing purpose {}", page, size);
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<UserResponseDto> users = userService.getallUsers(pageable);
        return ResponseEntity.ok(
                ResponseUtil.success(
                        users,
                        "Users fetched successfully.",
                        HttpStatus.OK,
                        request.getRequestURI()));
    }

    /**
     * Update User
     */
    @Operation(summary = "Update User")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResult<UserResponseDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto request,
            HttpServletRequest httpRequest) {
        UserResponseDto response = userService.updateUser(id, request);
        return ResponseEntity.ok(
                ResponseUtil.success(
                        response,
                        "User updated successfully.",
                        HttpStatus.OK,
                        httpRequest.getRequestURI()));
    }

    /**
     * Delete User
     */
    @Operation(summary = "Delete User")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResult<Object>> deleteUser(@PathVariable Long id,
                                           HttpServletRequest request) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ResponseUtil.success(null,"User deleted successfully.",
                        HttpStatus.OK,request.getRequestURI()));
    }

    /**
     *
     * @param username
     * @return Search user
     */
    @Operation(summary = "search users")
    @GetMapping("search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public ResponseEntity<ApiResult<List<UserResponseDto>>> searchUsers(
            @RequestParam String username,
            HttpServletRequest request
    ) {
        List<UserResponseDto> users = userService.searchUsers(username);
        return ResponseEntity.ok(
                ResponseUtil.success(
                        users,
                        "Users fetched successfully.",
                        HttpStatus.OK,
                        request.getRequestURI()));
    }
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "update user role")
    public ResponseEntity<ApiResult<UserResponseDto>> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody Role role,
            HttpServletRequest request
    ){
        UserResponseDto response = userService.updateUserRole(id, role);
        return ResponseEntity.ok(ResponseUtil.success(response,
        "User role updated successfully",
                HttpStatus.OK,
                request.getRequestURI()));
    }

    /**
     * Change Password
     */
    @Operation(summary = "Change user password",
            description = "Allows a user to change their own password by providing the current and new password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Current password is wrong or new password is same as current"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public ResponseEntity<ApiResult<Object>> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        userService.changePassword(id, request);
        return ResponseEntity.ok(ResponseUtil.success(
                null,
                "Password changed successfully.",
                HttpStatus.OK,
                httpRequest.getRequestURI()));
    }

    /**
     * Update own profile (firstName, lastName, email only)
     */
    @Operation(summary = "Update user profile",
            description = "Allows a user to update their own firstName, lastName and email.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public ResponseEntity<ApiResult<UserResponseDto>> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        UserResponseDto response = userService.updateProfile(id, request);
        return ResponseEntity.ok(ResponseUtil.success(
                response,
                "Profile updated successfully.",
                HttpStatus.OK,
                httpRequest.getRequestURI()));
    }
}
