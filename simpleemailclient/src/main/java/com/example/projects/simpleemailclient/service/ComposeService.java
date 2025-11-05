package com.example.projects.simpleemailclient.service;

import com.example.projects.simpleemailclient.model.Message;

import java.util.List;

/**
 * Service interface for composing and sending emails
 * Handles drafts, attachments, DKIM signing, and submission to MTA
 */
public interface ComposeService {

    /**
     * Create a new draft
     * @param draft Draft message
     * @return Created draft with ID
     */
    Message createDraft(Message draft);

    /**
     * Update an existing draft
     * @param draft Draft message with updates
     * @return Updated draft
     */
    Message updateDraft(Message draft);

    /**
     * Get draft by ID
     * @param draftId Draft ID
     * @param userId User ID for security check
     * @return Draft message
     */
    Message getDraft(Long draftId, Long userId);

    /**
     * Delete draft
     * @param draftId Draft ID
     * @param userId User ID for security check
     */
    void deleteDraft(Long draftId, Long userId);

    /**
     * Send email message
     * @param messageId Message ID
     * @param userId User ID for security check
     * @return Sent message
     */
    Message sendMessage(Long messageId, Long userId);

    /**
     * Add attachment to message
     * @param messageId Message ID
     * @param filename Attachment filename
     * @param contentType MIME type
     * @param data File data
     * @param userId User ID for security check
     * @return Blob pointer/key in MinIO
     */
    String addAttachment(Long messageId, String filename, String contentType, 
                        byte[] data, Long userId);

    /**
     * Remove attachment from message
     * @param messageId Message ID
     * @param attachmentId Attachment ID
     * @param userId User ID for security check
     */
    void removeAttachment(Long messageId, Long attachmentId, Long userId);

    /**
     * Reply to message
     * @param originalMessageId Original message ID
     * @param replyContent Reply content
     * @param userId User ID
     * @return Created reply draft
     */
    Message createReply(Long originalMessageId, String replyContent, Long userId);

    /**
     * Forward message
     * @param originalMessageId Original message ID
     * @param toAddresses Forward recipients
     * @param userId User ID
     * @return Created forward draft
     */
    Message createForward(Long originalMessageId, List<String> toAddresses, Long userId);

    /**
     * Validate recipients
     * @param recipients List of email addresses
     * @return true if all recipients are valid
     */
    boolean validateRecipients(List<String> recipients);

    /**
     * Check if user has sending privileges
     * @param userId User ID
     * @return true if user can send
     */
    boolean canSend(Long userId);

    /**
     * Get sending rate limit status
     * @param userId User ID
     * @return Number of emails user can still send today
     */
    int getRemainingDailyLimit(Long userId);
}
