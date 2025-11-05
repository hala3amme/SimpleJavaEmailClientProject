package com.example.projects.simpleemailclient.repository;

import com.example.projects.simpleemailclient.model.Rule;
import com.example.projects.simpleemailclient.model.Rule.RuleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Rule entity
 * Handles email filtering and automation rules
 */
@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {

    List<Rule> findByUserIdAndEnabledOrderByPriorityAsc(Long userId, Boolean enabled);

    List<Rule> findByUserIdOrderByPriorityAsc(Long userId);

    List<Rule> findByUserIdAndRuleType(Long userId, RuleType ruleType);

    @Query("SELECT r FROM Rule r WHERE r.userId = :userId AND r.enabled = true " +
           "ORDER BY r.priority ASC")
    List<Rule> findActiveRulesByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Rule r WHERE r.userId = :userId AND r.enabled = true")
    Long countActiveRulesByUserId(@Param("userId") Long userId);
}
