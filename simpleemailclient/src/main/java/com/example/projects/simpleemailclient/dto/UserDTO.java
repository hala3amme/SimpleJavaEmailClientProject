package com.example.projects.simpleemailclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for User entity
 * Used for API responses and requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private Long tenantId;
    private String email;
    private String status;
    private Long quotaBytes;
    private Long usedBytes;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean mfaEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    
    // Computed field
    private Double quotaUsagePercentage;
}
