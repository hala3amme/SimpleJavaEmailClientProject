package com.example.projects.simpleemailclient.controller;

import com.example.projects.simpleemailclient.dto.UserDTO;
import com.example.projects.simpleemailclient.model.User;
import com.example.projects.simpleemailclient.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for User management
 * Provides endpoints for user CRUD operations
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        log.info("Creating user: {}", userDTO.getEmail());
        
        User user = mapToEntity(userDTO);
        User createdUser = userService.createUser(user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(createdUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(user -> ResponseEntity.ok(mapToDTO(user)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
            .map(user -> ResponseEntity.ok(mapToDTO(user)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<UserDTO>> getUsersByTenant(@PathVariable Long tenantId) {
        List<UserDTO> users = userService.getUsersByTenant(tenantId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        User user = mapToEntity(userDTO);
        User updatedUser = userService.updateUser(user);
        
        return ResponseEntity.ok(mapToDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/quota/near-limit")
    public ResponseEntity<List<UserDTO>> getUsersNearQuota() {
        List<UserDTO> users = userService.getUsersNearQuota().stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(users);
    }

    // Simple mapping methods (in real app, use MapStruct)
    private UserDTO mapToDTO(User user) {
        Double usagePercentage = user.getQuotaBytes() > 0 
            ? (double) user.getUsedBytes() / user.getQuotaBytes() * 100 
            : 0.0;
        
        return UserDTO.builder()
            .id(user.getId())
            .tenantId(user.getTenantId())
            .email(user.getEmail())
            .status(user.getStatus().name())
            .quotaBytes(user.getQuotaBytes())
            .usedBytes(user.getUsedBytes())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole())
            .mfaEnabled(user.getMfaEnabled())
            .createdAt(user.getCreatedAt())
            .lastLoginAt(user.getLastLoginAt())
            .quotaUsagePercentage(usagePercentage)
            .build();
    }

    private User mapToEntity(UserDTO dto) {
        return User.builder()
            .id(dto.getId())
            .tenantId(dto.getTenantId())
            .email(dto.getEmail())
            .status(dto.getStatus() != null ? User.UserStatus.valueOf(dto.getStatus()) : null)
            .quotaBytes(dto.getQuotaBytes())
            .usedBytes(dto.getUsedBytes())
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .role(dto.getRole())
            .mfaEnabled(dto.getMfaEnabled())
            .build();
    }
}
