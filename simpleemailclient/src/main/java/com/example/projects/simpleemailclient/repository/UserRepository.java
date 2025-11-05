package com.example.projects.simpleemailclient.repository;

import com.example.projects.simpleemailclient.model.User;
import com.example.projects.simpleemailclient.model.User.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository interface for User entity
 * Provides database access methods for user management
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndTenantId(Long id, Long tenantId);

    List<User> findByTenantId(Long tenantId);

    Page<User> findByTenantId(Long tenantId, Pageable pageable);

    List<User> findByStatus(UserStatus status);

    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.status = :status")
    List<User> findByTenantIdAndStatus(@Param("tenantId") Long tenantId, 
                                       @Param("status") UserStatus status);

    @Query("SELECT u FROM User u WHERE u.usedBytes > u.quotaBytes * 0.9")
    List<User> findUsersNearQuota();

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.tenantId = :tenantId AND u.status = :status")
    Long countByTenantIdAndStatus(@Param("tenantId") Long tenantId, 
                                   @Param("status") UserStatus status);
}
