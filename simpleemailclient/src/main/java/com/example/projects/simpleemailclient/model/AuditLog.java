package com.example.projects.simpleemailclient.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * AuditLog entity for immutable audit trail
 * Append-only logs for compliance and security
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_actor", columnList = "actor_id"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_target", columnList = "target_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long actorId;

    @Column(length = 100)
    private String actorEmail;

    @Column(nullable = false, length = 100)
    private String action;

    @Column
    private Long targetId;

    @Column(length = 100)
    private String targetType;

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(columnDefinition = "TEXT")
    private String payloadJson;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AuditLevel level;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public enum AuditLevel {
        INFO,
        WARNING,
        CRITICAL,
        SECURITY
    }
}
