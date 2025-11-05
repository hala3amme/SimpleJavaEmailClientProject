package com.example.projects.simpleemailclient.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Attachment entity representing email attachments
 * Actual file content stored in MinIO (blob storage)
 */
@Entity
@Table(name = "attachments", indexes = {
    @Index(name = "idx_attachment_message", columnList = "message_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long messageId;

    @Column(nullable = false, length = 500)
    private String filename;

    @Column(nullable = false)
    private Long sizeBytes;

    @Column(nullable = false, length = 100)
    private String mimeType;

    @Column(nullable = false, length = 500)
    private String blobPointer;

    @Column(length = 64)
    private String checksumSha256;

    @Column(nullable = false)
    private Boolean isInline;

    @Column(length = 100)
    private String contentId;

    @Column(length = 50)
    private String scanStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;
}
