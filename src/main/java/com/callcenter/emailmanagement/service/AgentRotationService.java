package com.callcenter.emailmanagement.service;

import com.callcenter.emailmanagement.domain.model.Agent;
import com.callcenter.emailmanagement.domain.model.WorkQueueType;
import com.callcenter.emailmanagement.repository.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class AgentRotationService {
    private static final Logger logger = LoggerFactory.getLogger(AgentRotationService.class);
    
    @Autowired
    private AgentRepository agentRepository;
    
    private boolean initialized = false;
    
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void rotateAgentAvailability() {
        if (!initialized) {
            initializeTestAgents();
            initialized = true;
        }
        
        logger.info("🔄 Rotating agent availability...");
        
        List<Agent> agents = agentRepository.findAll();
        
        for (Agent agent : agents) {
            // Rotate availability: AVAILABLE -> BUSY -> AVAILABLE
            switch (agent.getStatus()) {
                case AVAILABLE:
                    agent.setStatus(Agent.AgentStatus.BUSY);
                    logger.info("🔴 Agent {} is now BUSY", agent.getAgentId());
                    break;
                case BUSY:
                    agent.setStatus(Agent.AgentStatus.AVAILABLE);
                    logger.info("🟢 Agent {} is now AVAILABLE", agent.getAgentId());
                    break;
                case OFFLINE:
                    agent.setStatus(Agent.AgentStatus.AVAILABLE);
                    logger.info("🟢 Agent {} came online and is AVAILABLE", agent.getAgentId());
                    break;
                default:
                    // Keep current status
                    break;
            }
            
            agent.setLastActiveTime(LocalDateTime.now());
            agentRepository.save(agent);
        }
        
        // Log current agent status
        logAgentStatus();
    }
    
    private void initializeTestAgents() {
        logger.info("🚀 Initializing test agents...");
        
        // Create Agent 1 - General Inquiry specialist
        Agent agent1 = new Agent("AGENT001", "John Smith", "john.smith@company.com");
        agent1.setSkillSets(Arrays.asList(WorkQueueType.GENERAL_INQUIRY));
        agent1.setStatus(Agent.AgentStatus.AVAILABLE);
        agent1.setMaxConcurrentCases(3);
        agent1.setCurrentCaseCount(0);
        
        // Create Agent 2 - Billing specialist
        Agent agent2 = new Agent("AGENT002", "Jane Doe", "jane.doe@company.com");
        agent2.setSkillSets(Arrays.asList(WorkQueueType.BILLING_SUPPORT));
        agent2.setStatus(Agent.AgentStatus.AVAILABLE);
        agent2.setMaxConcurrentCases(5);
        agent2.setCurrentCaseCount(0);
        
        agentRepository.save(agent1);
        agentRepository.save(agent2);
        
        logger.info("✅ Initialized 2 test agents");
        logger.info("👤 Agent 1: {} - Skills: {}", agent1.getName(), agent1.getSkillSets());
        logger.info("👤 Agent 2: {} - Skills: {}", agent2.getName(), agent2.getSkillSets());
    }
    
    private void logAgentStatus() {
        List<Agent> agents = agentRepository.findAll();
        
        logger.info("📊 Current Agent Status:");
        agents.forEach(agent -> {
            logger.info("   👤 {} ({}): {} - Cases: {}/{} - Skills: {}", 
                agent.getName(), 
                agent.getAgentId(),
                agent.getStatus(),
                agent.getCurrentCaseCount(),
                agent.getMaxConcurrentCases(),
                agent.getSkillSets()
            );
        });
    }
    
    public void updateAgentStatus(String agentId, Agent.AgentStatus status) {
        agentRepository.findByAgentId(agentId).ifPresent(agent -> {
            agent.setStatus(status);
            agent.setLastActiveTime(LocalDateTime.now());
            agentRepository.save(agent);
            logger.info("📝 Updated agent {} status to {}", agentId, status);
        });
    }
}