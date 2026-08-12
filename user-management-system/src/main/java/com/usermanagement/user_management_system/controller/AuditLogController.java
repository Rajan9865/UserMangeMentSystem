package com.usermanagement.user_management_system.controller;

import com.usermanagement.user_management_system.dto.AuditLogResponseDto;
import com.usermanagement.user_management_system.service.AuditLogService;
import com.usermanagement.user_management_system.util.ApiResult;
import com.usermanagement.user_management_system.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/29/2026
 */
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Audit Log API", description = "Activity feed and audit trail for user management operations")
@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Operation(summary = "Get audit logs",
            description = "Returns a paginated list of all user management activity. Restricted to ADMIN and MODERATOR.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResult<Page<AuditLogResponseDto>>> getLogs(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "")   String search,
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest request) {

        Page<AuditLogResponseDto> logs = auditLogService.getLogs(page, size, search, sortDir);
        return ResponseEntity.ok(ResponseUtil.success(
                logs,
                "Audit logs fetched successfully.",
                HttpStatus.OK,
                request.getRequestURI()));
    }
}
