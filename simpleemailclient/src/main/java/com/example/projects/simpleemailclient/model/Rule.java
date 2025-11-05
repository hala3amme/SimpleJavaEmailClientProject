package com.example.projects.simpleemailclient.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Rule entity for email filtering and automation
 * Supports Sieve-like execution for filters, vacation responder, etc.
 */
@Entity
@Table(name = "rules", indexes = {
    @Index(name = "idx_rule_user", columnList = "user_id"),
    @Index(name = "idx_rule_enabled", columnList = "enabled"),
    @Index(name = "idx_rule_priority", columnList = "priority")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private RuleType ruleType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conditionJson;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String actionJson;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private Integer priority;

    @Column
    private Long executionCount;

    @Column
    private LocalDateTime lastExecutedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public enum RuleType {
        FILTER,
        VACATION_RESPONDER,
        FORWARD,
        AUTO_REPLY,
        MOVE_TO_FOLDER,
        LABEL,
        DELETE
    }
}
