package com.example.projects.simpleemailclient.repository;

import com.example.projects.simpleemailclient.model.User;
import com.example.projects.simpleemailclient.model.User.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for UserRepository
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
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
    }

    @Test
    void save_Success() {
        // When
        User saved = userRepository.save(testUser);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findByEmail_UserExists_ReturnsUser() {
        // Given
        entityManager.persist(testUser);
        entityManager.flush();

        // When
        Optional<User> result = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByEmail_UserNotExists_ReturnsEmpty() {
        // When
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByIdAndTenantId_Success() {
        // Given
        User saved = entityManager.persist(testUser);
        entityManager.flush();

        // When
        Optional<User> result = userRepository.findByIdAndTenantId(saved.getId(), 100L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTenantId()).isEqualTo(100L);
    }

    @Test
    void findByIdAndTenantId_WrongTenant_ReturnsEmpty() {
        // Given
        User saved = entityManager.persist(testUser);
        entityManager.flush();

        // When
        Optional<User> result = userRepository.findByIdAndTenantId(saved.getId(), 999L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findByTenantId_ReturnsAllUsersForTenant() {
        // Given
        User user1 = entityManager.persist(testUser);
        
        User user2 = User.builder()
            .tenantId(100L)
            .email("user2@example.com")
            .status(UserStatus.ACTIVE)
            .quotaBytes(5368709120L)
            .usedBytes(0L)
            .mfaEnabled(false)
            .build();
        entityManager.persist(user2);

        User user3 = User.builder()
            .tenantId(200L)
            .email("user3@example.com")
            .status(UserStatus.ACTIVE)
            .quotaBytes(5368709120L)
            .usedBytes(0L)
            .mfaEnabled(false)
            .build();
        entityManager.persist(user3);
        entityManager.flush();

        // When
        List<User> result = userRepository.findByTenantId(100L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getTenantId).containsOnly(100L);
    }

    @Test
    void findByStatus_ReturnsUsersWithStatus() {
        // Given
        entityManager.persist(testUser);

        User suspendedUser = User.builder()
            .tenantId(100L)
            .email("suspended@example.com")
            .status(UserStatus.SUSPENDED)
            .quotaBytes(5368709120L)
            .usedBytes(0L)
            .mfaEnabled(false)
            .build();
        entityManager.persist(suspendedUser);
        entityManager.flush();

        // When
        List<User> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);
        List<User> suspendedUsers = userRepository.findByStatus(UserStatus.SUSPENDED);

        // Then
        assertThat(activeUsers).hasSize(1);
        assertThat(suspendedUsers).hasSize(1);
        assertThat(activeUsers.get(0).getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(suspendedUsers.get(0).getStatus()).isEqualTo(UserStatus.SUSPENDED);
    }

    @Test
    void findByTenantIdAndStatus_FiltersCorrectly() {
        // Given
        entityManager.persist(testUser);

        User activeUser2 = User.builder()
            .tenantId(100L)
            .email("active2@example.com")
            .status(UserStatus.ACTIVE)
            .quotaBytes(5368709120L)
            .usedBytes(0L)
            .mfaEnabled(false)
            .build();
        entityManager.persist(activeUser2);

        User inactiveUser = User.builder()
            .tenantId(100L)
            .email("inactive@example.com")
            .status(UserStatus.INACTIVE)
            .quotaBytes(5368709120L)
            .usedBytes(0L)
            .mfaEnabled(false)
            .build();
        entityManager.persist(inactiveUser);
        entityManager.flush();

        // When
        List<User> result = userRepository.findByTenantIdAndStatus(100L, UserStatus.ACTIVE);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getStatus).containsOnly(UserStatus.ACTIVE);
    }

    @Test
    void findUsersNearQuota_ReturnsUsersAbove90Percent() {
        // Given
        User nearQuotaUser = User.builder()
            .tenantId(100L)
            .email("nearquota@example.com")
            .status(UserStatus.ACTIVE)
            .quotaBytes(1000L)
            .usedBytes(950L) // 95%
            .mfaEnabled(false)
            .build();
        entityManager.persist(nearQuotaUser);

        User belowQuotaUser = User.builder()
            .tenantId(100L)
            .email("belowquota@example.com")
            .status(UserStatus.ACTIVE)
            .quotaBytes(1000L)
            .usedBytes(500L) // 50%
            .mfaEnabled(false)
            .build();
        entityManager.persist(belowQuotaUser);
        entityManager.flush();

        // When
        List<User> result = userRepository.findUsersNearQuota();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("nearquota@example.com");
    }

    @Test
    void existsByEmail_UserExists_ReturnsTrue() {
        // Given
        entityManager.persist(testUser);
        entityManager.flush();

        // When
        boolean result = userRepository.existsByEmail("test@example.com");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void existsByEmail_UserNotExists_ReturnsFalse() {
        // When
        boolean result = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void countByTenantIdAndStatus_ReturnsCorrectCount() {
        // Given
        entityManager.persist(testUser);

        User activeUser2 = User.builder()
            .tenantId(100L)
            .email("active2@example.com")
            .status(UserStatus.ACTIVE)
            .quotaBytes(5368709120L)
            .usedBytes(0L)
            .mfaEnabled(false)
            .build();
        entityManager.persist(activeUser2);

        User inactiveUser = User.builder()
            .tenantId(100L)
            .email("inactive@example.com")
            .status(UserStatus.INACTIVE)
            .quotaBytes(5368709120L)
            .usedBytes(0L)
            .mfaEnabled(false)
            .build();
        entityManager.persist(inactiveUser);
        entityManager.flush();

        // When
        Long activeCount = userRepository.countByTenantIdAndStatus(100L, UserStatus.ACTIVE);
        Long inactiveCount = userRepository.countByTenantIdAndStatus(100L, UserStatus.INACTIVE);

        // Then
        assertThat(activeCount).isEqualTo(2);
        assertThat(inactiveCount).isEqualTo(1);
    }

    @Test
    void update_OptimisticLocking_VersionIncremented() {
        // Given
        User saved = entityManager.persist(testUser);
        entityManager.flush();
        Long originalVersion = saved.getVersion();

        // When
        saved.setFirstName("Jane");
        entityManager.persist(saved);
        entityManager.flush();

        // Then
        assertThat(saved.getVersion()).isGreaterThan(originalVersion);
    }
}
