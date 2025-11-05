package com.example.projects.simpleemailclient.service;

import com.example.projects.simpleemailclient.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Message metadata management
 * Handles email message operations, threading, and flags
 */
public interface MessageMetadataService {

    /**
     * Create a new message
     * @param message Message entity to create
     * @return Created message with generated ID
     */
    Message createMessage(Message message);

    /**
     * Update an existing message
     * @param message Message entity with updated fields
     * @return Updated message
     */
    Message updateMessage(Message message);

    /**
     * Get message by ID
     * @param id Message ID
     * @param userId User ID for security check
     * @return Optional containing message if found and user has access
     */
    Optional<Message> getMessageById(Long id, Long userId);

    /**
     * Get message by UID
     * @param msgUid Message UID
     * @return Optional containing message if found
     */
    Optional<Message> getMessageByUid(String msgUid);

    /**
     * Get messages in a mailbox with pagination
     * @param userId User ID
     * @param mailboxId Mailbox ID
     * @param pageable Pagination parameters
     * @return Page of messages
     */
    Page<Message> getMailboxMessages(Long userId, Long mailboxId, Pageable pageable);

    /**
     * Get messages in a thread
     * @param threadId Thread ID
     * @return List of messages in the thread
     */
    List<Message> getThreadMessages(Long threadId);

    /**
     * Move message to different mailbox
     * @param messageId Message ID
     * @param targetMailboxId Target mailbox ID
     * @param userId User ID for security check
     * @return Updated message
     */
    Message moveMessage(Long messageId, Long targetMailboxId, Long userId);

    /**
     * Update message flags (READ, STARRED, etc.)
     * @param messageId Message ID
     * @param flags New flags string
     * @param userId User ID for security check
     * @return Updated message
     */
    Message updateFlags(Long messageId, String flags, Long userId);

    /**
     * Mark message as read
     * @param messageId Message ID
     * @param userId User ID for security check
     * @return Updated message
     */
    Message markAsRead(Long messageId, Long userId);

    /**
     * Mark message as unread
     * @param messageId Message ID
     * @param userId User ID for security check
     * @return Updated message
     */
    Message markAsUnread(Long messageId, Long userId);

    /**
     * Search messages by keyword
     * @param userId User ID
     * @param keyword Search keyword
     * @param pageable Pagination parameters
     * @return Page of matching messages
     */
    Page<Message> searchMessages(Long userId, String keyword, Pageable pageable);

    /**
     * Get messages with attachments
     * @param userId User ID
     * @param pageable Pagination parameters
     * @return Page of messages with attachments
     */
    Page<Message> getMessagesWithAttachments(Long userId, Pageable pageable);

    /**
     * Get messages in date range
     * @param userId User ID
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination parameters
     * @return Page of messages in range
     */
    Page<Message> getMessagesByDateRange(Long userId, LocalDateTime startDate, 
                                         LocalDateTime endDate, Pageable pageable);

    /**
     * Delete message
     * @param messageId Message ID
     * @param userId User ID for security check
     */
    void deleteMessage(Long messageId, Long userId);

    /**
     * Calculate thread ID for message
     * @param message Message to thread
     * @return Thread ID
     */
    Long calculateThreadId(Message message);

    /**
     * Get total storage used by user
     * @param userId User ID
     * @return Total bytes used
     */
    Long getTotalStorageUsed(Long userId);
}
