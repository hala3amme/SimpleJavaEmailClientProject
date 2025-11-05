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
 * Message entity representing email messages with metadata
 * Actual MIME content stored in MinIO (blob storage)
 */
@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_message_user", columnList = "user_id"),
    @Index(name = "idx_message_mailbox", columnList = "mailbox_id"),
    @Index(name = "idx_message_thread", columnList = "thread_id"),
    @Index(name = "idx_message_date", columnList = "message_date"),
    @Index(name = "idx_message_uid", columnList = "msg_uid")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long mailboxId;

    @Column(nullable = false, unique = true)
    private String msgUid;

    @Column(nullable = false, length = 1000)
    private String subject;

    @Column(nullable = false, length = 500)
    private String fromAddress;

    @Column(nullable = false, length = 2000)
    private String toAddresses;

    @Column(length = 2000)
    private String ccAddresses;

    @Column(length = 500)
    private String bccHash;

    @Column(nullable = false)
    private LocalDateTime messageDate;

    @Column(nullable = false)
    private Long sizeBytes;

    @Column(nullable = false, length = 100)
    private String flags;

    @Column
    private Long threadId;

    @Column(length = 500)
    private String snippet;

    @Column(nullable = false, length = 500)
    private String mimePointer;

    @Column(length = 100)
    private String messageId;

    @Column(length = 500)
    private String inReplyTo;

    @Column(length = 1000)
    private String references;

    @Column(length = 50)
    private String dkimResult;

    @Column
    private Double spamScore;

    @Column(nullable = false)
    private Boolean hasAttachments;

    @Column(length = 100)
    private String contentType;

    @Column(length = 50)
    private String priority;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;
}
