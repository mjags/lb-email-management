package com.callcenter.emailmanagement.repository;

import com.callcenter.emailmanagement.domain.model.Agent;
import com.callcenter.emailmanagement.domain.model.WorkQueueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    
    Optional<Agent> findByAgentId(String agentId);
    
    @Query("SELECT a FROM Agent a WHERE a.status = 'AVAILABLE' AND a.currentCaseCount < a.maxConcurrentCases")
    List<Agent> findAvailableAgents();
    
    @Query("SELECT a FROM Agent a WHERE a.status = 'AVAILABLE' AND a.currentCaseCount < a.maxConcurrentCases AND :queueType MEMBER OF a.skillSets")
    List<Agent> findAvailableAgentsBySkill(@Param("queueType") WorkQueueType queueType);
    
    List<Agent> findByStatus(Agent.AgentStatus status);
}