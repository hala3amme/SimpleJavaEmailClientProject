package com.example.projects.simpleemailclient.event;

import lombok.*;

/**
 * Event published when a message is updated (flags, mailbox move, etc.)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageUpdatedEvent extends BaseEvent {
    private Long messageId;
    private Long mailboxId;
    private String updateType;
    private String oldValue;
    private String newValue;
}
