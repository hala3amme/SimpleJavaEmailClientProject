package com.example.projects.simpleemailclient.service;

import com.example.projects.simpleemailclient.model.Mailbox;
import com.example.projects.simpleemailclient.model.Mailbox.MailboxType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Mailbox management
 * Handles folder operations, unread counts, and mailbox hierarchy
 */
public interface MailboxService {

    /**
     * Create a new mailbox
     * @param mailbox Mailbox entity to create
     * @return Created mailbox with generated ID
     */
    Mailbox createMailbox(Mailbox mailbox);

    /**
     * Update an existing mailbox
     * @param mailbox Mailbox entity with updated fields
     * @return Updated mailbox
     */
    Mailbox updateMailbox(Mailbox mailbox);

    /**
     * Get mailbox by ID
     * @param id Mailbox ID
     * @param userId User ID for security check
     * @return Optional containing mailbox if found and user has access
     */
    Optional<Mailbox> getMailboxById(Long id, Long userId);

    /**
     * Get all mailboxes for a user
     * @param userId User ID
     * @return List of mailboxes sorted by sort order
     */
    List<Mailbox> getUserMailboxes(Long userId);

    /**
     * Get specific mailbox by type
     * @param userId User ID
     * @param type Mailbox type
     * @return Optional containing mailbox if found
     */
    Optional<Mailbox> getMailboxByType(Long userId, MailboxType type);

    /**
     * Create default mailboxes for new user
     * @param userId User ID
     * @return List of created default mailboxes (INBOX, SENT, DRAFTS, TRASH, SPAM)
     */
    List<Mailbox> createDefaultMailboxes(Long userId);

    /**
     * Update unread count for mailbox
     * @param mailboxId Mailbox ID
     * @param delta Change in unread count (can be positive or negative)
     */
    void updateUnreadCount(Long mailboxId, int delta);

    /**
     * Update total message count for mailbox
     * @param mailboxId Mailbox ID
     * @param delta Change in total count (can be positive or negative)
     */
    void updateTotalCount(Long mailboxId, int delta);

    /**
     * Recalculate mailbox counts from messages
     * @param mailboxId Mailbox ID
     */
    void recalculateCounts(Long mailboxId);

    /**
     * Get mailboxes with unread messages
     * @param userId User ID
     * @return List of mailboxes with unread count > 0
     */
    List<Mailbox> getMailboxesWithUnread(Long userId);

    /**
     * Delete a mailbox
     * @param mailboxId Mailbox ID
     * @param userId User ID for security check
     */
    void deleteMailbox(Long mailboxId, Long userId);

    /**
     * Rename a mailbox
     * @param mailboxId Mailbox ID
     * @param newName New name
     * @param userId User ID for security check
     * @return Updated mailbox
     */
    Mailbox renameMailbox(Long mailboxId, String newName, Long userId);
}
