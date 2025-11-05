package com.example.projects.simpleemailclient.service;

import com.example.projects.simpleemailclient.model.User;
import com.example.projects.simpleemailclient.model.User.UserStatus;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for User management operations
 * Handles user lifecycle, authentication, and quota management
 */
public interface UserService {

    /**
     * Create a new user
     * @param user User entity to create
     * @return Created user with generated ID
     */
    User createUser(User user);

    /**
     * Update an existing user
     * @param user User entity with updated fields
     * @return Updated user
     */
    User updateUser(User user);

    /**
     * Find user by ID
     * @param id User ID
     * @return Optional containing user if found
     */
    Optional<User> getUserById(Long id);

    /**
     * Find user by email address
     * @param email Email address
     * @return Optional containing user if found
     */
    Optional<User> getUserByEmail(String email);

    /**
     * Get all users for a tenant
     * @param tenantId Tenant ID
     * @return List of users
     */
    List<User> getUsersByTenant(Long tenantId);

    /**
     * Update user status
     * @param userId User ID
     * @param status New status
     * @return Updated user
     */
    User updateUserStatus(Long userId, UserStatus status);

    /**
     * Update user quota
     * @param userId User ID
     * @param quotaBytes New quota in bytes
     * @return Updated user
     */
    User updateQuota(Long userId, Long quotaBytes);

    /**
     * Check if user has sufficient quota
     * @param userId User ID
     * @param additionalBytes Bytes to check
     * @return true if user has enough quota
     */
    boolean hasQuota(Long userId, Long additionalBytes);

    /**
     * Update used storage for user
     * @param userId User ID
     * @param deltaBytes Change in storage (positive or negative)
     */
    void updateUsedStorage(Long userId, Long deltaBytes);

    /**
     * Get users approaching quota limit
     * @return List of users near quota (>90%)
     */
    List<User> getUsersNearQuota();

    /**
     * Delete user (soft delete by setting status)
     * @param userId User ID
     */
    void deleteUser(Long userId);
}
