package com.example.projects.simpleemailclient.service;

import com.example.projects.simpleemailclient.model.AuditLog;
import com.example.projects.simpleemailclient.model.AuditLog.AuditLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service interface for audit logging and compliance
 * Provides immutable audit trail for security and compliance requirements
 */
public interface AuditService {

    /**
     * Log an audit event
     * @param actorId ID of user performing the action
     * @param actorEmail Email of actor
     * @param action Action performed
     * @param targetId ID of target entity
     * @param targetType Type of target entity
     * @param level Audit level
     * @param payload Additional data as JSON
     * @param ipAddress Actor's IP address
     * @param userAgent Actor's user agent
     * @return Created audit log entry
     */
    AuditLog log(Long actorId, String actorEmail, String action, Long targetId, 
                 String targetType, AuditLevel level, String payload, 
                 String ipAddress, String userAgent);

    /**
     * Log simple audit event
     * @param actorId ID of user performing the action
     * @param action Action performed
     * @param targetId ID of target entity
     * @param targetType Type of target entity
     * @return Created audit log entry
     */
    AuditLog log(Long actorId, String action, Long targetId, String targetType);

    /**
     * Log security event
     * @param actorId ID of user
     * @param action Action performed
     * @param payload Additional security context
     * @param ipAddress IP address
     * @return Created audit log entry
     */
    AuditLog logSecurityEvent(Long actorId, String action, Map<String, Object> payload, 
                              String ipAddress);

    /**
     * Get audit logs for a specific user (actor)
     * @param actorId Actor ID
     * @param pageable Pagination parameters
     * @return Page of audit logs
     */
    Page<AuditLog> getAuditLogsByActor(Long actorId, Pageable pageable);

    /**
     * Get audit logs for a specific action
     * @param action Action name
     * @param pageable Pagination parameters
     * @return Page of audit logs
     */
    Page<AuditLog> getAuditLogsByAction(String action, Pageable pageable);

    /**
     * Get audit logs for a specific target
     * @param targetId Target ID
     * @param targetType Target type
     * @return List of audit logs for the target
     */
    List<AuditLog> getAuditLogsByTarget(Long targetId, String targetType);

    /**
     * Get audit logs by level since a specific time
     * @param level Audit level
     * @param since Time threshold
     * @return List of audit logs
     */
    List<AuditLog> getAuditLogsByLevelSince(AuditLevel level, LocalDateTime since);

    /**
     * Get audit logs in date range for an actor
     * @param actorId Actor ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of audit logs
     */
    List<AuditLog> getAuditLogsByActorAndDateRange(Long actorId, LocalDateTime startDate, 
                                                    LocalDateTime endDate);

    /**
     * Export audit logs for compliance
     * @param startDate Start date
     * @param endDate End date
     * @return Export data (could be CSV, JSON, etc.)
     */
    byte[] exportAuditLogs(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get security events in time range
     * @param since Time threshold
     * @return List of security-level audit logs
     */
    List<AuditLog> getSecurityEvents(LocalDateTime since);

    /**
     * Archive old audit logs to WORM storage
     * @param olderThan Archive logs older than this date
     * @return Number of logs archived
     */
    long archiveOldLogs(LocalDateTime olderThan);
}
