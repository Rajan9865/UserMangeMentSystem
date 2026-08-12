package com.usermanagement.user_management_system.service.impl;

import com.usermanagement.user_management_system.dto.AuditLogResponseDto;
import com.usermanagement.user_management_system.entity.AuditLog;
import com.usermanagement.user_management_system.enums.AuditAction;
import com.usermanagement.user_management_system.repository.AuditLogRepository;
import com.usermanagement.user_management_system.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/29/2026
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(AuditAction action, Long targetUserId, String targetUsername,
                    String performedBy, String details) {
        AuditLog entry = AuditLog.builder()
                .action(action)
                .targetUserId(targetUserId)
                .targetUsername(targetUsername)
                .performedBy(performedBy)
                .details(details)
                .build();
        auditLogRepository.save(entry);
        log.info("Audit: action={} target={} by={}", action, targetUsername, performedBy);
    }

    @Override
    public Page<AuditLogResponseDto> getLogs(int page, int size, String search, String sortDir) {
        Sort sort = "asc".equalsIgnoreCase(sortDir)
                ? Sort.by("timestamp").ascending()
                : Sort.by("timestamp").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AuditLog> logs;
        if (search != null && !search.isBlank()) {
            logs = auditLogRepository
                    .findByPerformedByContainingIgnoreCaseOrTargetUsernameContainingIgnoreCase(
                            search, search, pageable);
        } else {
            logs = auditLogRepository.findAll(pageable);
        }

        return logs.map(this::toDto);
    }

    private AuditLogResponseDto toDto(AuditLog log) {
        return AuditLogResponseDto.builder()
                .id(log.getId())
                .action(log.getAction().name())
                .targetUserId(log.getTargetUserId())
                .targetUsername(log.getTargetUsername())
                .performedBy(log.getPerformedBy())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }
}
