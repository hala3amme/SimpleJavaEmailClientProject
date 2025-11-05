package com.example.projects.simpleemailclient.service;

import java.util.Map;

/**
 * Service interface for real-time notifications
 * Handles WebSocket push events and notification delivery
 */
public interface NotificationService {

    /**
     * Send new mail notification
     * @param userId User ID
     * @param messageId Message ID
     * @param mailboxId Mailbox ID
     */
    void notifyNewMail(Long userId, Long messageId, Long mailboxId);

    /**
     * Send mailbox update notification
     * @param userId User ID
     * @param mailboxId Mailbox ID
     * @param updateType Type of update (count_changed, new_folder, etc.)
     */
    void notifyMailboxUpdate(Long userId, Long mailboxId, String updateType);

    /**
     * Send message flag update notification
     * @param userId User ID
     * @param messageId Message ID
     * @param flags New flags
     */
    void notifyFlagUpdate(Long userId, Long messageId, String flags);

    /**
     * Send general notification
     * @param userId User ID
     * @param type Notification type
     * @param payload Notification payload
     */
    void sendNotification(Long userId, String type, Map<String, Object> payload);

    /**
     * Send quota warning notification
     * @param userId User ID
     * @param usedPercentage Percentage of quota used
     */
    void notifyQuotaWarning(Long userId, Double usedPercentage);

    /**
     * Send email delivery status notification
     * @param userId User ID
     * @param messageId Message ID
     * @param status Delivery status
     */
    void notifyDeliveryStatus(Long userId, Long messageId, String status);

    /**
     * Subscribe user to notifications
     * @param userId User ID
     * @param sessionId WebSocket session ID
     */
    void subscribe(Long userId, String sessionId);

    /**
     * Unsubscribe user from notifications
     * @param userId User ID
     * @param sessionId WebSocket session ID
     */
    void unsubscribe(Long userId, String sessionId);

    /**
     * Check if user is connected
     * @param userId User ID
     * @return true if user has active WebSocket connection
     */
    boolean isUserConnected(Long userId);

    /**
     * Send push notification to mobile device
     * @param userId User ID
     * @param title Notification title
     * @param body Notification body
     * @param data Additional data
     */
    void sendPushNotification(Long userId, String title, String body, Map<String, Object> data);
}
