package com.example.projects.simpleemailclient.repository;

import com.example.projects.simpleemailclient.model.Mailbox;
import com.example.projects.simpleemailclient.model.Mailbox.MailboxType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Mailbox entity
 * Handles folder/mailbox operations
 */
@Repository
public interface MailboxRepository extends JpaRepository<Mailbox, Long> {

    List<Mailbox> findByUserId(Long userId);

    List<Mailbox> findByUserIdOrderBySortOrderAsc(Long userId);

    Optional<Mailbox> findByUserIdAndMailboxType(Long userId, MailboxType mailboxType);

    Optional<Mailbox> findByIdAndUserId(Long id, Long userId);

    List<Mailbox> findByUserIdAndParentMailboxId(Long userId, Long parentMailboxId);

    @Query("SELECT m FROM Mailbox m WHERE m.userId = :userId AND m.unreadCount > 0")
    List<Mailbox> findMailboxesWithUnreadMessages(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Mailbox m SET m.unreadCount = m.unreadCount + :delta WHERE m.id = :mailboxId")
    void incrementUnreadCount(@Param("mailboxId") Long mailboxId, @Param("delta") int delta);

    @Modifying
    @Query("UPDATE Mailbox m SET m.totalCount = m.totalCount + :delta WHERE m.id = :mailboxId")
    void incrementTotalCount(@Param("mailboxId") Long mailboxId, @Param("delta") int delta);

    boolean existsByUserIdAndName(Long userId, String name);
}
