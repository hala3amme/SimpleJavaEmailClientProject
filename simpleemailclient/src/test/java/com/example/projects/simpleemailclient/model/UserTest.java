package com.example.projects.simpleemailclient.model;

import com.example.projects.simpleemailclient.model.User.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for User entity
 */
class UserTest {

    @Test
    void builder_CreateUser_Success() {
        // When
        User user = User.builder()
            .id(1L)
            .tenantId(100L)
            .email("test@example.com")
            .status(UserStatus.ACTIVE)
            .quotaBytes(5368709120L)
            .usedBytes(1073741824L)
            .firstName("John")
            .lastName("Doe")
            .role("USER")
            .mfaEnabled(false)
            .build();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getQuotaBytes()).isEqualTo(5368709120L);
        assertThat(user.getUsedBytes()).isEqualTo(1073741824L);
        assertThat(user.getMfaEnabled()).isFalse();
    }

    @Test
    void userStatus_AllValuesAccessible() {
        // Then
        assertThat(UserStatus.ACTIVE).isNotNull();
        assertThat(UserStatus.INACTIVE).isNotNull();
        assertThat(UserStatus.SUSPENDED).isNotNull();
        assertThat(UserStatus.PENDING_VERIFICATION).isNotNull();
        assertThat(UserStatus.DELETED).isNotNull();
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        // Given
        User user = new User();

        // When
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setStatus(UserStatus.ACTIVE);
        user.setQuotaBytes(5368709120L);
        user.setUsedBytes(1073741824L);
        user.setMfaEnabled(true);

        // Then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getQuotaBytes()).isEqualTo(5368709120L);
        assertThat(user.getUsedBytes()).isEqualTo(1073741824L);
        assertThat(user.getMfaEnabled()).isTrue();
    }

    @Test
    void equals_SameId_ReturnsTrue() {
        // Given
        User user1 = User.builder().id(1L).email("test1@example.com").build();
        User user2 = User.builder().id(1L).email("test2@example.com").build();

        // Then
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    void equals_DifferentId_ReturnsFalse() {
        // Given
        User user1 = User.builder().id(1L).email("test@example.com").build();
        User user2 = User.builder().id(2L).email("test@example.com").build();

        // Then
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    void hashCode_SameId_SameHashCode() {
        // Given
        User user1 = User.builder().id(1L).email("test1@example.com").build();
        User user2 = User.builder().id(1L).email("test2@example.com").build();

        // Then
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }
}
