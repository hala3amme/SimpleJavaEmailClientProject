package com.example.projects.simpleemailclient.service.impl;

import com.example.projects.simpleemailclient.model.User;
import com.example.projects.simpleemailclient.model.User.UserStatus;
import com.example.projects.simpleemailclient.repository.UserRepository;
import com.example.projects.simpleemailclient.service.UserService;
import com.example.projects.simpleemailclient.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService
 * Handles user lifecycle, authentication, and quota management
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuditService auditService;

    @Override
    public User createUser(User user) {
        log.info("Creating new user: {}", user.getEmail());
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        // Set defaults
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.PENDING_VERIFICATION);
        }
        if (user.getUsedBytes() == null) {
            user.setUsedBytes(0L);
        }
        if (user.getMfaEnabled() == null) {
            user.setMfaEnabled(false);
        }

        User savedUser = userRepository.save(user);
        
        auditService.log(savedUser.getId(), "USER_CREATED", savedUser.getId(), "USER");
        
        log.info("User created successfully: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    public User updateUser(User user) {
        log.info("Updating user: {}", user.getId());
        
        User existingUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + user.getId()));

        // Prevent email changes
        user.setEmail(existingUser.getEmail());
        
        User updatedUser = userRepository.save(user);
        
        auditService.log(user.getId(), "USER_UPDATED", user.getId(), "USER");
        
        return updatedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByTenant(Long tenantId) {
        return userRepository.findByTenantId(tenantId);
    }

    @Override
    public User updateUserStatus(Long userId, UserStatus status) {
        log.info("Updating user status for user {}: {}", userId, status);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        UserStatus oldStatus = user.getStatus();
        user.setStatus(status);
        
        User updatedUser = userRepository.save(user);
        
        auditService.log(userId, "USER_STATUS_CHANGED", userId, "USER");
        
        log.info("User status updated from {} to {} for user {}", oldStatus, status, userId);
        return updatedUser;
    }

    @Override
    public User updateQuota(Long userId, Long quotaBytes) {
        log.info("Updating quota for user {}: {} bytes", userId, quotaBytes);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        user.setQuotaBytes(quotaBytes);
        
        User updatedUser = userRepository.save(user);
        
        auditService.log(userId, "USER_QUOTA_UPDATED", userId, "USER");
        
        return updatedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasQuota(Long userId, Long additionalBytes) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        return (user.getUsedBytes() + additionalBytes) <= user.getQuotaBytes();
    }

    @Override
    public void updateUsedStorage(Long userId, Long deltaBytes) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        long newUsedBytes = user.getUsedBytes() + deltaBytes;
        
        if (newUsedBytes < 0) {
            newUsedBytes = 0;
        }
        
        if (newUsedBytes > user.getQuotaBytes()) {
            throw new IllegalStateException("Storage quota exceeded for user: " + userId);
        }
        
        user.setUsedBytes(newUsedBytes);
        userRepository.save(user);
        
        // Log if approaching quota (>90%)
        double usagePercent = (double) newUsedBytes / user.getQuotaBytes() * 100;
        if (usagePercent > 90) {
            log.warn("User {} is at {}% of quota", userId, usagePercent);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersNearQuota() {
        return userRepository.findUsersNearQuota();
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Deleting user: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        // Soft delete by setting status
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
        
        auditService.log(userId, "USER_DELETED", userId, "USER");
        
        log.info("User deleted: {}", userId);
    }
}
