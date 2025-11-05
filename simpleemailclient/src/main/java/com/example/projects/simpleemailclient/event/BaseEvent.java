package com.example.projects.simpleemailclient.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Base event class for all domain events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent {
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private Long userId;
    private String correlationId;
}
