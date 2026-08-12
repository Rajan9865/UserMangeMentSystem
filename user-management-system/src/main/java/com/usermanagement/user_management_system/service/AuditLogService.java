package com.usermanagement.user_management_system.service;

import com.usermanagement.user_management_system.dto.AuditLogResponseDto;
import com.usermanagement.user_management_system.enums.AuditAction;
import org.springframework.data.domain.Page;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/29/2026
 */
public interface AuditLogService {

    void log(AuditAction action, Long targetUserId, String targetUsername,
             String performedBy, String details);

    Page<AuditLogResponseDto> getLogs(int page, int size, String search, String sortDir);
}
