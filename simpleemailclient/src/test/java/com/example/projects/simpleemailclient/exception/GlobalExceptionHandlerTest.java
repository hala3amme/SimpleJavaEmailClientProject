package com.example.projects.simpleemailclient.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GlobalExceptionHandler
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFoundException_ReturnsNotFound() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("User", 123L);
        WebRequest request = createWebRequest("/api/v1/users/123");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).contains("User with id 123 not found");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/users/123");
    }

    @Test
    void handleQuotaExceededException_ReturnsForbidden() {
        // Given
        QuotaExceededException exception = new QuotaExceededException(1L, 5368709120L);
        WebRequest request = createWebRequest("/api/v1/messages");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleQuotaExceededException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(403);
        assertThat(response.getBody().getMessage()).contains("exceeded quota");
    }

    @Test
    void handleIllegalArgumentException_ReturnsBadRequest() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("Invalid email format");
        WebRequest request = createWebRequest("/api/v1/users");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid email format");
    }

    @Test
    void handleIllegalStateException_ReturnsConflict() {
        // Given
        IllegalStateException exception = new IllegalStateException("User already exists");
        WebRequest request = createWebRequest("/api/v1/users");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalStateException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).isEqualTo("User already exists");
    }

    @Test
    void handleGlobalException_ReturnsInternalServerError() {
        // Given
        Exception exception = new Exception("Unexpected error occurred");
        WebRequest request = createWebRequest("/api/v1/users");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(exception, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().getDetails()).contains("Unexpected error occurred");
    }

    @Test
    void errorResponse_ContainsTimestamp() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("Test not found");
        WebRequest request = createWebRequest("/api/test");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFoundException(exception, request);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    private WebRequest createWebRequest(String uri) {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI(uri);
        return new ServletWebRequest(servletRequest);
    }
}
