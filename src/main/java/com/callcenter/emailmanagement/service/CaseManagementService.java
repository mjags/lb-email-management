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
import java.util.UUID;

@Service
public class CaseManagementService {
    private static final Logger logger = LoggerFactory.getLogger(CaseManagementService.class);
    
    @Autowired
    private WorkQueueService workQueueService;
    
    @Autowired
    private SlaTrackingService slaTrackingService;
    
    @Autowired
    private CaseRepository caseRepository;
    
    @Autowired
    private EmailRepository emailRepository;
    
    public Case createCaseFromEmail(Email email) {
        logger.info("Creating case from email: {}", email.getSubject());
        
        Case newCase = new Case();
        newCase.setCaseNumber(generateCaseNumber());
        newCase.setCustomerEmail(email.getFromAddress());
        newCase.setSubject(email.getSubject());
        newCase.setDescription(extractDescription(email));
        newCase.setQueueType(determineQueueType(email));
        newCase.setPriority(determineCasePriority(email));
        
        // Link email to case
        newCase.addEmail(email);
        email.setEmailCase(newCase);
        
        // Persist case to database
        Case savedCase = caseRepository.save(newCase);
        emailRepository.save(email);
        
        logger.info("📋 Case persisted: {} with email: {}", savedCase.getCaseNumber(), email.getMessageId());
        
        // Initialize SLA tracking
        slaTrackingService.initializeSlaTracking(savedCase);
        
        // Add to work queue
        workQueueService.addCaseToQueue(savedCase);
        
        logger.info("✅ Case created successfully: {}", savedCase.getCaseNumber());
        return savedCase;
    }
    
    public void updateCase(Case caseEntity, String updateDetails) {
        logger.info("Updating case: {}", caseEntity.getCaseNumber());
        
        caseEntity.setLastModifiedDate(LocalDateTime.now());
        
        // Persist changes to database
        caseRepository.save(caseEntity);
        
        // Update SLA tracking
        slaTrackingService.updateSlaStatus(caseEntity);
        
        logger.info("Case updated successfully: {}", caseEntity.getCaseNumber());
    }
    
    public void assignCaseToAgent(Case caseEntity, Agent agent) {
        logger.info("Assigning case {} to agent {}", caseEntity.getCaseNumber(), agent.getAgentId());
        
        caseEntity.setAssignedAgent(agent);
        caseEntity.setStatus(Case.CaseStatus.ASSIGNED);
        
        // Update agent's case count
        agent.setCurrentCaseCount(agent.getCurrentCaseCount() + 1);
        
        // Persist changes
        caseRepository.save(caseEntity);
        
        logger.info("🎯 Case {} assigned to agent {}", caseEntity.getCaseNumber(), agent.getAgentId());
        
        logger.info("Case assigned successfully");
    }
    
    public void resolveCase(Case caseEntity, String resolutionNotes) {
        logger.info("Resolving case: {}", caseEntity.getCaseNumber());
        
        caseEntity.setStatus(Case.CaseStatus.RESOLVED);
        caseEntity.setResolvedDate(LocalDateTime.now());
        
        // Update SLA tracking
        slaTrackingService.markResolutionTime(caseEntity);
        
        // Update agent's case count
        if (caseEntity.getAssignedAgent() != null) {
            Agent agent = caseEntity.getAssignedAgent();
            agent.setCurrentCaseCount(Math.max(0, agent.getCurrentCaseCount() - 1));
        }
        
        // Persist changes
        caseRepository.save(caseEntity);
        
        logger.info("🎉 Case resolved: {} - Resolution time: {}", caseEntity.getCaseNumber(), caseEntity.getResolvedDate());
        
        logger.info("Case resolved successfully: {}", caseEntity.getCaseNumber());
    }
    
    public List<Case> getCasesByAgent(Agent agent) {
        logger.debug("Fetching cases for agent: {}", agent.getAgentId());
        
        return caseRepository.findByAssignedAgent(agent);
    }
    
    public List<Case> getCasesByStatus(Case.CaseStatus status) {
        logger.debug("Fetching cases by status: {}", status);
        
        return caseRepository.findByStatus(status);
    }
    
    public Optional<Case> findByCaseNumber(String caseNumber) {
        logger.debug("Finding case by number: {}", caseNumber);
        
        return caseRepository.findByCaseNumber(caseNumber);
    }
    
    private String generateCaseNumber() {
        // Generate unique case number with prefix
        return "CASE-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String extractDescription(Email email) {
        // Extract meaningful description from email content
        String content = email.getTextContent() != null ? email.getTextContent() : email.getHtmlContent();
        
        if (content != null && content.length() > 500) {
            return content.substring(0, 500) + "...";
        }
        
        return content;
    }
    
    private WorkQueueType determineQueueType(Email email) {
        // TODO: Implement intelligent queue routing based on email content
        // TODO: Use ML/NLP for better classification
        
        String subject = email.getSubject().toLowerCase();
        String content = (email.getTextContent() != null ? email.getTextContent() : "").toLowerCase();
        
        if (subject.contains("bill") || subject.contains("payment") || subject.contains("invoice") ||
            content.contains("billing") || content.contains("payment")) {
            return WorkQueueType.BILLING_SUPPORT;
        }
        
        return WorkQueueType.GENERAL_INQUIRY;
    }
    
    private Case.CasePriority determineCasePriority(Email email) {
        // TODO: Implement priority determination logic
        // TODO: Consider customer tier, keywords, SLA requirements
        
        Email.EmailPriority emailPriority = email.getPriority();
        
        return switch (emailPriority) {
            case URGENT -> Case.CasePriority.URGENT;
            case HIGH -> Case.CasePriority.HIGH;
            case LOW -> Case.CasePriority.LOW;
            default -> Case.CasePriority.NORMAL;
        };
    }
}