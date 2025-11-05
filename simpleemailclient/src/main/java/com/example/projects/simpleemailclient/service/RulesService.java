package com.example.projects.simpleemailclient.service;

import com.example.projects.simpleemailclient.model.Rule;
import com.example.projects.simpleemailclient.model.Message;
import com.example.projects.simpleemailclient.model.Rule.RuleType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for email rules and automation
 * Implements Sieve-like filtering, vacation responders, and auto-forwarding
 */
public interface RulesService {

    /**
     * Create a new rule
     * @param rule Rule entity to create
     * @return Created rule with generated ID
     */
    Rule createRule(Rule rule);

    /**
     * Update an existing rule
     * @param rule Rule entity with updates
     * @return Updated rule
     */
    Rule updateRule(Rule rule);

    /**
     * Get rule by ID
     * @param ruleId Rule ID
     * @param userId User ID for security check
     * @return Optional containing rule if found and user has access
     */
    Optional<Rule> getRuleById(Long ruleId, Long userId);

    /**
     * Get all rules for a user
     * @param userId User ID
     * @return List of rules ordered by priority
     */
    List<Rule> getUserRules(Long userId);

    /**
     * Get active rules for a user
     * @param userId User ID
     * @return List of enabled rules ordered by priority
     */
    List<Rule> getActiveRules(Long userId);

    /**
     * Get rules by type
     * @param userId User ID
     * @param ruleType Rule type
     * @return List of rules of specified type
     */
    List<Rule> getRulesByType(Long userId, RuleType ruleType);

    /**
     * Enable a rule
     * @param ruleId Rule ID
     * @param userId User ID for security check
     * @return Updated rule
     */
    Rule enableRule(Long ruleId, Long userId);

    /**
     * Disable a rule
     * @param ruleId Rule ID
     * @param userId User ID for security check
     * @return Updated rule
     */
    Rule disableRule(Long ruleId, Long userId);

    /**
     * Delete a rule
     * @param ruleId Rule ID
     * @param userId User ID for security check
     */
    void deleteRule(Long ruleId, Long userId);

    /**
     * Apply rules to a message
     * @param message Message to apply rules to
     * @return List of applied rule IDs
     */
    List<Long> applyRules(Message message);

    /**
     * Apply specific rule to a message
     * @param ruleId Rule ID
     * @param message Message to apply rule to
     * @return true if rule was applied successfully
     */
    boolean applyRule(Long ruleId, Message message);

    /**
     * Evaluate rule condition against message
     * @param rule Rule to evaluate
     * @param message Message to check
     * @return true if condition matches
     */
    boolean evaluateCondition(Rule rule, Message message);

    /**
     * Execute rule action on message
     * @param rule Rule with action to execute
     * @param message Message to act upon
     */
    void executeAction(Rule rule, Message message);

    /**
     * Update rule priority
     * @param ruleId Rule ID
     * @param newPriority New priority value
     * @param userId User ID for security check
     * @return Updated rule
     */
    Rule updatePriority(Long ruleId, Integer newPriority, Long userId);

    /**
     * Validate rule syntax
     * @param conditionJson Condition JSON
     * @param actionJson Action JSON
     * @return true if valid
     */
    boolean validateRule(String conditionJson, String actionJson);
}
