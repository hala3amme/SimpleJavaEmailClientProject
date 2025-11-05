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
 * Mailbox entity representing mail folders (INBOX, SENT, DRAFTS, etc.)
 */
@Entity
@Table(name = "mailboxes", indexes = {
    @Index(name = "idx_mailbox_user", columnList = "user_id"),
    @Index(name = "idx_mailbox_type", columnList = "mailbox_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mailbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private MailboxType mailboxType;

    @Column(nullable = false)
    private Integer unreadCount;

    @Column(nullable = false)
    private Integer totalCount;

    @Column
    private Long parentMailboxId;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(length = 500)
    private String attributes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public enum MailboxType {
        INBOX,
        SENT,
        DRAFTS,
        TRASH,
        SPAM,
        ARCHIVE,
        CUSTOM
    }
}
