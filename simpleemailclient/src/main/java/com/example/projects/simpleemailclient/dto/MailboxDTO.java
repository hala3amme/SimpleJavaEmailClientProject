package com.example.projects.simpleemailclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Mailbox entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailboxDTO {
    private Long id;
    private String name;
    private String mailboxType;
    private Integer unreadCount;
    private Integer totalCount;
    private Long parentMailboxId;
    private Integer sortOrder;
}
