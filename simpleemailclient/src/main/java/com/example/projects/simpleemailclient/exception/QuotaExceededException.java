package com.example.projects.simpleemailclient.exception;

/**
 * Custom exception for quota exceeded scenarios
 */
public class QuotaExceededException extends RuntimeException {
    public QuotaExceededException(String message) {
        super(message);
    }

    public QuotaExceededException(Long userId, Long quotaBytes) {
        super(String.format("User %d has exceeded quota of %d bytes", userId, quotaBytes));
    }
}
