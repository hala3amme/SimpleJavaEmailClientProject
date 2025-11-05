package com.example.projects.simpleemailclient.event;

import lombok.*;

/**
 * Event published when a new message is ingested
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageIngestedEvent extends BaseEvent {
    private Long messageId;
    private Long mailboxId;
    private String msgUid;
    private String subject;
    private String fromAddress;
    private Long sizeBytes;
    private Boolean hasAttachments;
}
