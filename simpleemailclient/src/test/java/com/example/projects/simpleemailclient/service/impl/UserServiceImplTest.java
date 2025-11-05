package com.example.projects.simpleemailclient.service.impl;

import com.example.projects.simpleemailclient.model.User;
import com.example.projects.simpleemailclient.model.User.UserStatus;
import com.example.projects.simpleemailclient.repository.UserRepository;
import com.example.projects.simpleemailclient.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .tenantId(100L)
            .email("test@example.com")
            .status(UserStatus.ACTIVE)
            .quotaBytes(5368709120L) // 5GB
            .usedBytes(1073741824L)  // 1GB
            .firstName("John")
            .lastName("Doe")
            .role("USER")
            .mfaEnabled(false)
            .build();
    }

    @Test
    void createUser_Success() {
        // Given
        User newUser = User.builder()
            .email("newuser@example.com")
            .tenantId(100L)
            .quotaBytes(5368709120L)
            .build();

        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User createdUser = userService.createUser(newUser);

        // Then
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getStatus()).isEqualTo(UserStatus.PENDING_VERIFICATION);
        assertThat(createdUser.getUsedBytes()).isEqualTo(0L);
        assertThat(createdUser.getMfaEnabled()).isFalse();
        
        verify(userRepository).existsByEmail(newUser.getEmail());
        verify(userRepository).save(any(User.class));
        verify(auditService).log(anyLong(), eq("USER_CREATED"), anyLong(), eq("USER"));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.createUser(testUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already exists");

        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_UserNotExists_ReturnsEmpty() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(999L);

        // Then
        assertThat(result).isEmpty();

        verify(userRepository).findById(999L);
    }

    @Test
    void getUserByEmail_Success() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void getUsersByTenant_Success() {
        // Given
        User user2 = User.builder()
            .id(2L)
            .tenantId(100L)
            .email("user2@example.com")
            .status(UserStatus.ACTIVE)
            .build();

        List<User> tenantUsers = Arrays.asList(testUser, user2);
        when(userRepository.findByTenantId(100L)).thenReturn(tenantUsers);

        // When
        List<User> result = userService.getUsersByTenant(100L);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getTenantId).containsOnly(100L);

        verify(userRepository).findByTenantId(100L);
    }

    @Test
    void updateUserStatus_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User updatedUser = userService.updateUserStatus(1L, UserStatus.SUSPENDED);

        // Then
        assertThat(updatedUser.getStatus()).isEqualTo(UserStatus.SUSPENDED);

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        verify(auditService).log(eq(1L), eq("USER_STATUS_CHANGED"), eq(1L), eq("USER"));
    }

    @Test
    void updateUserStatus_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.updateUserStatus(999L, UserStatus.SUSPENDED))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User not found");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateQuota_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User updatedUser = userService.updateQuota(1L, 10737418240L); // 10GB

        // Then
        assertThat(updatedUser.getQuotaBytes()).isEqualTo(10737418240L);

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
        verify(auditService).log(eq(1L), eq("USER_QUOTA_UPDATED"), eq(1L), eq("USER"));
    }

    @Test
    void hasQuota_SufficientQuota_ReturnsTrue() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.hasQuota(1L, 1073741824L); // Request 1GB, has 4GB available

        // Then
        assertThat(result).isTrue();

        verify(userRepository).findById(1L);
    }

    @Test
    void hasQuota_InsufficientQuota_ReturnsFalse() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.hasQuota(1L, 5368709120L); // Request 5GB, only 4GB available

        // Then
        assertThat(result).isFalse();

        verify(userRepository).findById(1L);
    }

    @Test
    void updateUsedStorage_IncrementSuccess() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateUsedStorage(1L, 1073741824L); // Add 1GB

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(argThat(user -> 
            user.getUsedBytes().equals(2147483648L) // 2GB total
        ));
    }

    @Test
    void updateUsedStorage_DecrementSuccess() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateUsedStorage(1L, -536870912L); // Remove 512MB

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(argThat(user -> 
            user.getUsedBytes().equals(536870912L) // 512MB remaining
        ));
    }

    @Test
    void updateUsedStorage_ExceedsQuota_ThrowsException() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When/Then
        assertThatThrownBy(() -> userService.updateUsedStorage(1L, 10737418240L)) // Add 10GB
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("quota exceeded");

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUsedStorage_NegativeResultBecomesZero() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateUsedStorage(1L, -2147483648L); // Remove more than available

        // Then
        verify(userRepository).save(argThat(user -> 
            user.getUsedBytes().equals(0L)
        ));
    }

    @Test
    void getUsersNearQuota_Success() {
        // Given
        User nearQuotaUser = User.builder()
            .id(2L)
            .quotaBytes(1000L)
            .usedBytes(950L) // 95% usage
            .build();

        when(userRepository.findUsersNearQuota()).thenReturn(Arrays.asList(nearQuotaUser));

        // When
        List<User> result = userService.getUsersNearQuota();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(2L);

        verify(userRepository).findUsersNearQuota();
    }

    @Test
    void deleteUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(argThat(user -> 
            user.getStatus() == UserStatus.DELETED
        ));
        verify(auditService).log(eq(1L), eq("USER_DELETED"), eq(1L), eq("USER"));
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.deleteUser(999L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User not found");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_Success() {
        // Given
        User updatedUser = User.builder()
            .id(1L)
            .email("test@example.com") // Same email
            .firstName("Jane")
            .lastName("Smith")
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.updateUser(updatedUser);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).findById(1L);
        verify(userRepository).save(updatedUser);
        verify(auditService).log(eq(1L), eq("USER_UPDATED"), eq(1L), eq("USER"));
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        // Given
        User updatedUser = User.builder().id(999L).build();
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> userService.updateUser(updatedUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User not found");
    }
}
