package com.example.projects.simpleemailclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Message entity
 * Used for API responses with essential message information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private Long mailboxId;
    private String msgUid;
    private String subject;
    private String fromAddress;
    private String toAddresses;
    private String ccAddresses;
    private LocalDateTime messageDate;
    private Long sizeBytes;
    private String flags;
    private Long threadId;
    private String snippet;
    private Boolean hasAttachments;
    private String priority;
    private LocalDateTime createdAt;
}
