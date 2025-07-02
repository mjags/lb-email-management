package com.callcenter.emailmanagement.service;

import com.callcenter.emailmanagement.domain.model.*;
import com.callcenter.emailmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkQueueService {
    private static final Logger logger = LoggerFactory.getLogger(WorkQueueService.class);
    
    @Autowired
    private WorkQueueRepository workQueueRepository;
    
    @Autowired
    private AgentRepository agentRepository;
    
    public void addCaseToQueue(Case caseEntity) {
        logger.info("Adding case {} to work queue: {}", caseEntity.getCaseNumber(), caseEntity.getQueueType());
        
        WorkQueue queueItem = new WorkQueue(caseEntity, caseEntity.getQueueType());
        
        // Persist to database
        workQueueRepository.save(queueItem);
        
        logger.info("🎯 Case {} added to {} queue with priority {}", 
            caseEntity.getCaseNumber(), caseEntity.getQueueType(), queueItem.getPriorityScore());
        
        // Notify available agents
        notifyAvailableAgents(caseEntity.getQueueType());
        
        logger.info("✅ Case added to queue successfully");
    }
    
    public Optional<Case> getNextCaseForAgent(Agent agent, WorkQueueType queueType) {
        logger.info("Getting next case for agent {} from queue {}", agent.getAgentId(), queueType);
        
        if (!agent.isAvailable()) {
            logger.warn("Agent {} is not available for new cases", agent.getAgentId());
            return Optional.empty();
        }
        
        if (!agent.getSkillSets().contains(queueType)) {
            logger.warn("Agent {} does not have skills for queue type {}", agent.getAgentId(), queueType);
            return Optional.empty();
        }
        
        // Find next case from queue
        Optional<WorkQueue> nextQueueItem = workQueueRepository.findNextPendingByQueueType(queueType.name());
        
        if (nextQueueItem.isPresent()) {
            Case nextCase = nextQueueItem.get().getCaseItem();
            assignCaseToAgent(nextCase, agent);
            
            // Update queue item status
            WorkQueue queueItem = nextQueueItem.get();
            queueItem.setStatus(WorkQueue.QueueStatus.ASSIGNED);
            queueItem.setAssignedAgent(agent);
            workQueueRepository.save(queueItem);
            
            logger.info("🎯 Assigned case {} to agent {}", nextCase.getCaseNumber(), agent.getAgentId());
            return Optional.of(nextCase);
        }
        
        logger.info("📭 No cases available in queue {} for agent {}", queueType, agent.getAgentId());
        return Optional.empty();
    }
    
    public List<WorkQueue> getPendingQueueItems(WorkQueueType queueType) {
        logger.debug("Fetching pending queue items for: {}", queueType);
        
        return workQueueRepository.findPendingByQueueTypeOrderedByPriority(queueType);
    }
    
    public List<Agent> getAvailableAgents(WorkQueueType queueType) {
        logger.debug("Finding available agents for queue type: {}", queueType);
        
        return agentRepository.findAvailableAgentsBySkill(queueType);
    }
    
    public void updateQueueItemStatus(WorkQueue queueItem, WorkQueue.QueueStatus status) {
        logger.info("Updating queue item {} status to {}", queueItem.getId(), status);
        
        queueItem.setStatus(status);
        
        // Persist changes to database
        workQueueRepository.save(queueItem);
        
        logger.info("Queue item status updated successfully");
    }
    
    public int getQueueDepth(WorkQueueType queueType) {
        logger.debug("Getting queue depth for: {}", queueType);
        
        return (int) workQueueRepository.countByQueueTypeAndStatus(queueType, WorkQueue.QueueStatus.PENDING);
    }
    
    public double getAverageWaitTime(WorkQueueType queueType) {
        logger.debug("Calculating average wait time for: {}", queueType);
        
        // TODO: Implement calculation based on historical data
        // TODO: Consider time from queue entry to assignment
        
        return 0.0;
    }
    
    private void assignCaseToAgent(Case caseEntity, Agent agent) {
        logger.info("Assigning case {} to agent {}", caseEntity.getCaseNumber(), agent.getAgentId());
        
        caseEntity.setAssignedAgent(agent);
        caseEntity.setStatus(Case.CaseStatus.ASSIGNED);
        
        // Update agent status and case count
        agent.setCurrentCaseCount(agent.getCurrentCaseCount() + 1);
        agent.setLastActiveTime(LocalDateTime.now());
        
        // TODO: Update work queue item status
        // TODO: Send notification to agent
        // TODO: Persist changes
        
        logger.info("Case assigned successfully");
    }
    
    private Optional<Case> findHighestPriorityCase(WorkQueueType queueType) {
        logger.debug("Finding highest priority case for queue: {}", queueType);
        
        // TODO: Implement actual database query
        // TODO: Order by priority score, waiting time, SLA urgency
        
        // Placeholder implementation
        return Optional.empty();
    }
    
    private void notifyAvailableAgents(WorkQueueType queueType) {
        logger.info("Notifying available agents for queue type: {}", queueType);
        
        List<Agent> availableAgents = getAvailableAgents(queueType);
        
        for (Agent agent : availableAgents) {
            sendNotificationToAgent(agent, queueType);
        }
        
        logger.info("Notifications sent to {} agents", availableAgents.size());
    }
    
    private void sendNotificationToAgent(Agent agent, WorkQueueType queueType) {
        logger.debug("Sending notification to agent {} for queue {}", agent.getAgentId(), queueType);
        
        // TODO: Implement actual notification mechanism
        // TODO: Consider WebSocket, push notifications, or email
        // TODO: Include queue metrics and priority information
        
        logger.debug("Notification sent to agent: {}", agent.getAgentId());
    }
    
    public void redistributeUnassignedCases() {
        logger.info("Starting redistribution of unassigned cases");
        
        // TODO: Find cases that have been in queue too long
        // TODO: Reassess priorities and agent availability
        // TODO: Implement load balancing across agents
        
        logger.info("Case redistribution completed");
    }
    
    public WorkQueueMetrics getQueueMetrics(WorkQueueType queueType) {
        logger.debug("Generating metrics for queue: {}", queueType);
        
        // TODO: Implement metrics calculation
        // TODO: Include average wait time, queue depth, throughput
        
        return new WorkQueueMetrics(queueType, 0, 0.0, 0.0);
    }
    
    public static class WorkQueueMetrics {
        private final WorkQueueType queueType;
        private final int queueDepth;
        private final double averageWaitTime;
        private final double throughputPerHour;
        
        public WorkQueueMetrics(WorkQueueType queueType, int queueDepth, double averageWaitTime, double throughputPerHour) {
            this.queueType = queueType;
            this.queueDepth = queueDepth;
            this.averageWaitTime = averageWaitTime;
            this.throughputPerHour = throughputPerHour;
        }
        
        public WorkQueueType getQueueType() { return queueType; }
        public int getQueueDepth() { return queueDepth; }
        public double getAverageWaitTime() { return averageWaitTime; }
        public double getThroughputPerHour() { return throughputPerHour; }
    }
}