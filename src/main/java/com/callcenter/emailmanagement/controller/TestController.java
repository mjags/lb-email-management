package com.callcenter.emailmanagement.controller;

import com.callcenter.emailmanagement.domain.model.*;
import com.callcenter.emailmanagement.service.*;
import com.callcenter.emailmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    @Autowired
    private CaseManagementService caseManagementService;
    
    @Autowired
    private EmailRepository emailRepository;
    
    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private WorkQueueService workQueueService;
    
    @PostMapping("/simulate-email")
    public ResponseEntity<String> simulateIncomingEmail(
            @RequestParam(defaultValue = "test@customer.com") String fromEmail,
            @RequestParam(defaultValue = "Test Support Request") String subject,
            @RequestParam(defaultValue = "I need help with my account") String content) {
        
        logger.info("🎬 Simulating incoming email from: {}", fromEmail);
        
        try {
            // Create test email
            Email testEmail = new Email();
            testEmail.setMessageId("TEST-" + System.currentTimeMillis());
            testEmail.setFromAddress(fromEmail);
            testEmail.setToAddress("support@company.com");
            testEmail.setSubject(subject);
            testEmail.setTextContent(content);
            testEmail.setDirection(Email.EmailDirection.INBOUND);
            testEmail.setPriority(Email.EmailPriority.NORMAL);
            testEmail.setReceivedDate(LocalDateTime.now());
            
            // Save email
            Email savedEmail = emailRepository.save(testEmail);
            logger.info("📧 Test email saved: {}", savedEmail.getMessageId());
            
            // Create case from email
            Case newCase = caseManagementService.createCaseFromEmail(savedEmail);
            logger.info("📋 Case created: {}", newCase.getCaseNumber());
            
            return ResponseEntity.ok("✅ Test email processed - Case: " + newCase.getCaseNumber());
            
        } catch (Exception e) {
            logger.error("❌ Error simulating email: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("❌ Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/agent-status")
    public ResponseEntity<String> getAgentStatus() {
        StringBuilder status = new StringBuilder();
        
        agentRepository.findAll().forEach(agent -> {
            status.append(String.format("Agent %s (%s): %s - Cases: %d/%d%n", 
                agent.getAgentId(), agent.getName(), agent.getStatus(), 
                agent.getCurrentCaseCount(), agent.getMaxConcurrentCases()));
        });
        
        return ResponseEntity.ok(status.toString());
    }
    
    @GetMapping("/queue-status")
    public ResponseEntity<String> getQueueStatus() {
        StringBuilder status = new StringBuilder();
        
        for (WorkQueueType queueType : WorkQueueType.values()) {
            int depth = workQueueService.getQueueDepth(queueType);
            status.append(String.format("Queue %s: %d pending cases%n", queueType, depth));
        }
        
        return ResponseEntity.ok(status.toString());
    }
}