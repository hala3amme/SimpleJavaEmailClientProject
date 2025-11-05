package com.example.projects.simpleemailclient.repository;

import com.example.projects.simpleemailclient.model.AuditLog;
import com.example.projects.simpleemailclient.model.AuditLog.AuditLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for AuditLog entity
 * Provides immutable audit trail access
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByActorIdOrderByTimestampDesc(Long actorId, Pageable pageable);

    Page<AuditLog> findByActionOrderByTimestampDesc(String action, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.actorId = :actorId AND " +
           "a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp DESC")
    List<AuditLog> findByActorIdAndDateRange(@Param("actorId") Long actorId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM AuditLog a WHERE a.level = :level AND " +
           "a.timestamp > :since ORDER BY a.timestamp DESC")
    List<AuditLog> findByLevelSince(@Param("level") AuditLevel level,
                                    @Param("since") LocalDateTime since);

    @Query("SELECT a FROM AuditLog a WHERE a.targetId = :targetId AND " +
           "a.targetType = :targetType ORDER BY a.timestamp DESC")
    List<AuditLog> findByTarget(@Param("targetId") Long targetId,
                                @Param("targetType") String targetType);
}

