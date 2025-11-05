package com.example.projects.simpleemailclient.repository;

import com.example.projects.simpleemailclient.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Message entity
 * Handles email message metadata operations
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findByMsgUid(String msgUid);

    Optional<Message> findByIdAndUserId(Long id, Long userId);

    Page<Message> findByMailboxIdOrderByMessageDateDesc(Long mailboxId, Pageable pageable);

    Page<Message> findByUserIdAndMailboxIdOrderByMessageDateDesc(Long userId, Long mailboxId, Pageable pageable);

    List<Message> findByThreadId(Long threadId);

    @Query("SELECT m FROM Message m WHERE m.userId = :userId AND m.mailboxId = :mailboxId " +
           "AND m.flags LIKE %:flag% ORDER BY m.messageDate DESC")
    Page<Message> findByUserIdAndMailboxIdAndFlag(@Param("userId") Long userId, 
                                                   @Param("mailboxId") Long mailboxId,
                                                   @Param("flag") String flag, 
                                                   Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.userId = :userId " +
           "AND m.messageDate BETWEEN :startDate AND :endDate " +
           "ORDER BY m.messageDate DESC")
    Page<Message> findByUserIdAndDateRange(@Param("userId") Long userId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.userId = :userId AND m.hasAttachments = true " +
           "ORDER BY m.messageDate DESC")
    Page<Message> findMessagesWithAttachments(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.mailboxId = :mailboxId AND m.flags NOT LIKE '%READ%'")
    Long countUnreadByMailboxId(@Param("mailboxId") Long mailboxId);

    @Query("SELECT SUM(m.sizeBytes) FROM Message m WHERE m.userId = :userId")
    Long calculateTotalSizeByUserId(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE m.userId = :userId AND " +
           "(LOWER(m.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.fromAddress) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Message> searchMessages(@Param("userId") Long userId, 
                                 @Param("keyword") String keyword, 
                                 Pageable pageable);

    void deleteByMailboxId(Long mailboxId);
}
