package com.example.projects.simpleemailclient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service interface for email search operations
 * Integrates with OpenSearch for full-text and header search
 */
public interface SearchService {

    /**
     * Search messages by query
     * @param userId User ID
     * @param query Search query string
     * @param pageable Pagination parameters
     * @return Page of search results with message IDs and snippets
     */
    Page<SearchResult> searchMessages(Long userId, String query, Pageable pageable);

    /**
     * Search by sender
     * @param userId User ID
     * @param fromAddress Sender email address
     * @param pageable Pagination parameters
     * @return Page of search results
     */
    Page<SearchResult> searchBySender(Long userId, String fromAddress, Pageable pageable);

    /**
     * Search by subject
     * @param userId User ID
     * @param subject Subject keyword
     * @param pageable Pagination parameters
     * @return Page of search results
     */
    Page<SearchResult> searchBySubject(Long userId, String subject, Pageable pageable);

    /**
     * Advanced search with multiple criteria
     * @param userId User ID
     * @param criteria Search criteria map
     * @param pageable Pagination parameters
     * @return Page of search results
     */
    Page<SearchResult> advancedSearch(Long userId, Map<String, Object> criteria, Pageable pageable);

    /**
     * Search messages in date range
     * @param userId User ID
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination parameters
     * @return Page of search results
     */
    Page<SearchResult> searchByDateRange(Long userId, LocalDateTime startDate, 
                                         LocalDateTime endDate, Pageable pageable);

    /**
     * Index message in search engine
     * @param messageId Message ID to index
     */
    void indexMessage(Long messageId);

    /**
     * Bulk index messages
     * @param messageIds List of message IDs to index
     */
    void bulkIndexMessages(Iterable<Long> messageIds);

    /**
     * Remove message from search index
     * @param messageId Message ID to remove
     */
    void removeFromIndex(Long messageId);

    /**
     * Reindex all messages for a user
     * @param userId User ID
     */
    void reindexUserMessages(Long userId);

    /**
     * Save search query for later use
     * @param userId User ID
     * @param queryName Query name
     * @param query Query string
     */
    void saveSearch(Long userId, String queryName, String query);

    /**
     * Get saved searches for user
     * @param userId User ID
     * @return Map of saved search names to queries
     */
    Map<String, String> getSavedSearches(Long userId);

    /**
     * Search result model
     */
    interface SearchResult {
        Long getMessageId();
        String getSubject();
        String getFromAddress();
        LocalDateTime getMessageDate();
        String getSnippet();
        Double getScore();
    }
}
