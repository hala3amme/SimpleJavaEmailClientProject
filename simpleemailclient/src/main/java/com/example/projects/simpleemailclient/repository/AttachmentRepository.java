package com.example.projects.simpleemailclient.repository;

import com.example.projects.simpleemailclient.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Attachment entity
 */
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByMessageId(Long messageId);

    @Query("SELECT a FROM Attachment a WHERE a.messageId IN :messageIds")
    List<Attachment> findByMessageIds(@Param("messageIds") List<Long> messageIds);

    @Query("SELECT SUM(a.sizeBytes) FROM Attachment a WHERE a.messageId = :messageId")
    Long calculateTotalSizeByMessageId(@Param("messageId") Long messageId);

    @Query("SELECT a FROM Attachment a WHERE a.scanStatus = :status")
    List<Attachment> findByScanStatus(@Param("status") String status);

    void deleteByMessageId(Long messageId);
}
