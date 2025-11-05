package com.example.projects.simpleemailclient.controller;

import com.example.projects.simpleemailclient.dto.UserDTO;
import com.example.projects.simpleemailclient.model.User;
import com.example.projects.simpleemailclient.model.User.UserStatus;
import com.example.projects.simpleemailclient.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer tests for UserController
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
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

        testUserDTO = UserDTO.builder()
            .id(1L)
            .tenantId(100L)
            .email("test@example.com")
            .status("ACTIVE")
            .quotaBytes(5368709120L)
            .usedBytes(1073741824L)
            .firstName("John")
            .lastName("Doe")
            .role("USER")
            .mfaEnabled(false)
            .quotaUsagePercentage(20.0)
            .build();
    }

    @Test
    void createUser_Success() throws Exception {
        // Given
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // When/Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(userService).createUser(any(User.class));
    }

    @Test
    void getUserById_UserExists_ReturnsUser() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        // When/Then
        mockMvc.perform(get("/api/v1/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_UserNotExists_ReturnsNotFound() throws Exception {
        // Given
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/v1/users/999"))
            .andExpect(status().isNotFound());

        verify(userService).getUserById(999L);
    }

    @Test
    void getUserByEmail_Success() throws Exception {
        // Given
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When/Then
        mockMvc.perform(get("/api/v1/users/email/test@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService).getUserByEmail("test@example.com");
    }

    @Test
    void getUsersByTenant_Success() throws Exception {
        // Given
        User user2 = User.builder()
            .id(2L)
            .tenantId(100L)
            .email("user2@example.com")
            .status(UserStatus.ACTIVE)
            .quotaBytes(5368709120L)
            .usedBytes(0L)
            .mfaEnabled(false)
            .build();

        when(userService.getUsersByTenant(100L)).thenReturn(Arrays.asList(testUser, user2));

        // When/Then
        mockMvc.perform(get("/api/v1/users/tenant/100"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].tenantId").value(100))
            .andExpect(jsonPath("$[1].tenantId").value(100));

        verify(userService).getUsersByTenant(100L);
    }

    @Test
    void updateUser_Success() throws Exception {
        // Given
        User updatedUser = User.builder()
            .id(1L)
            .email("test@example.com")
            .firstName("Jane")
            .lastName("Smith")
            .build();

        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        UserDTO updateDTO = UserDTO.builder()
            .firstName("Jane")
            .lastName("Smith")
            .build();

        // When/Then
        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Jane"))
            .andExpect(jsonPath("$.lastName").value("Smith"));

        verify(userService).updateUser(any(User.class));
    }

    @Test
    void deleteUser_Success() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When/Then
        mockMvc.perform(delete("/api/v1/users/1"))
            .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void getUsersNearQuota_Success() throws Exception {
        // Given
        User nearQuotaUser = User.builder()
            .id(2L)
            .quotaBytes(1000L)
            .usedBytes(950L)
            .email("nearquota@example.com")
            .status(UserStatus.ACTIVE)
            .mfaEnabled(false)
            .build();

        when(userService.getUsersNearQuota()).thenReturn(Arrays.asList(nearQuotaUser));

        // When/Then
        mockMvc.perform(get("/api/v1/users/quota/near-limit"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].quotaUsagePercentage").value(greaterThan(90.0)));

        verify(userService).getUsersNearQuota();
    }
}
