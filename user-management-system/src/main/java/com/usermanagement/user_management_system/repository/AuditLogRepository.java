package com.usermanagement.user_management_system.repository;

import com.usermanagement.user_management_system.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lenovo
 * @version 1.0
 * Practise_Project
 * @since 7/29/2026
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByPerformedByContainingIgnoreCaseOrTargetUsernameContainingIgnoreCase(
            String performedBy, String targetUsername, Pageable pageable);
}
