package com.callcenter.emailmanagement.controller;

import com.callcenter.emailmanagement.domain.model.*;
import com.callcenter.emailmanagement.service.*;
import com.callcenter.emailmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agents")
@CrossOrigin(origins = "*")
public class AgentController {
    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);
    
    @Autowired
    private WorkQueueService workQueueService;
    
    @Autowired
    private CaseManagementService caseManagementService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SlaTrackingService slaTrackingService;
    
    @Autowired
    private OutlookEmailService outlookEmailService;
    
    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private CaseRepository caseRepository;
    
    @Autowired
    private EmailRepository emailRepository;
    
    @PostMapping("/{agentId}/status")
    public ResponseEntity<String> updateAgentStatus(
            @PathVariable String agentId, 
            @RequestParam Agent.AgentStatus status) {
        // TODO: Implement agent status update
        // TODO: Validate agent exists
        // TODO: Update agent availability in database
        
        return ResponseEntity.ok("Agent status updated successfully");
    }
    
    @GetMapping("/{agentId}/next-case")
    public ResponseEntity<Case> getNextCase(
            @PathVariable String agentId,
            @RequestParam WorkQueueType queueType) {
        
        Optional<Agent> agent = agentRepository.findByAgentId(agentId);
        if (agent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<Case> nextCase = workQueueService.getNextCaseForAgent(agent.get(), queueType);
        
        return nextCase.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.noContent().build());
    }
    
    @GetMapping("/{agentId}/cases")
    public ResponseEntity<List<Case>> getAgentCases(@PathVariable String agentId) {
        // TODO: Implement agent lookup
        // TODO: Get assigned cases for agent
        
        List<Case> cases = List.of(); // caseManagementService.getCasesByAgent(agent);
        
        return ResponseEntity.ok(cases);
    }
    
    @PostMapping("/{agentId}/cases/{caseNumber}/respond")
    public ResponseEntity<String> respondToCase(
            @PathVariable String agentId,
            @PathVariable String caseNumber,
            @RequestBody EmailResponse emailResponse) {
        
        try {
            Optional<Agent> agent = agentRepository.findByAgentId(agentId);
            Optional<Case> caseOpt = caseRepository.findByCaseNumber(caseNumber);
            
            if (agent.isEmpty() || caseOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Case emailCase = caseOpt.get();
            
            // Create response email
            Email responseEmail = new Email();
            responseEmail.setMessageId("RESPONSE-" + System.currentTimeMillis());
            responseEmail.setFromAddress(agent.get().getEmail());
            responseEmail.setToAddress(emailResponse.getToAddress());
            responseEmail.setSubject(emailResponse.getSubject());
            responseEmail.setTextContent(emailResponse.getContent());
            responseEmail.setDirection(Email.EmailDirection.OUTBOUND);
            responseEmail.setPriority(emailResponse.getPriority());
            responseEmail.setEmailCase(emailCase);
            
            // Send email
            outlookEmailService.sendEmail(responseEmail);
            
            // Save response email
            emailRepository.save(responseEmail);
            emailCase.addEmail(responseEmail);
            
            // Update case status
            emailCase.setStatus(Case.CaseStatus.IN_PROGRESS);
            caseRepository.save(emailCase);
            
            // Mark first response in SLA tracking
            slaTrackingService.markFirstResponse(emailCase);
            
            return ResponseEntity.ok("✅ Response sent successfully");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("❌ Error sending response: " + e.getMessage());
        }
    }
    
    @PostMapping("/{agentId}/cases/{caseNumber}/resolve")
    public ResponseEntity<String> resolveCase(
            @PathVariable String agentId,
            @PathVariable String caseNumber,
            @RequestBody CaseResolution resolution) {
        
        // TODO: Validate agent and case
        // TODO: Update case status to resolved
        // TODO: Update SLA tracking
        // TODO: Send customer notification
        
        return ResponseEntity.ok("Case resolved successfully");
    }
    
    @GetMapping("/{agentId}/workload")
    public ResponseEntity<AgentWorkload> getAgentWorkload(@PathVariable String agentId) {
        // TODO: Implement agent lookup
        // TODO: Calculate current workload metrics
        
        AgentWorkload workload = new AgentWorkload(agentId, 0, 5, List.of());
        
        return ResponseEntity.ok(workload);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<Agent>> getAvailableAgents(@RequestParam WorkQueueType queueType) {
        List<Agent> availableAgents = workQueueService.getAvailableAgents(queueType);
        
        return ResponseEntity.ok(availableAgents);
    }
    
    @PostMapping("/{agentId}/cases/{caseNumber}/update")
    public ResponseEntity<String> updateCase(
            @PathVariable String agentId,
            @PathVariable String caseNumber,
            @RequestBody CaseUpdate caseUpdate) {
        
        // TODO: Validate agent and case
        // TODO: Update case with new information
        // TODO: Update SLA tracking
        
        return ResponseEntity.ok("Case updated successfully");
    }
    
    // DTOs
    public static class EmailResponse {
        private String toAddress;
        private String subject;
        private String content;
        private Email.EmailPriority priority;
        
        // Getters and Setters
        public String getToAddress() { return toAddress; }
        public void setToAddress(String toAddress) { this.toAddress = toAddress; }
        
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public Email.EmailPriority getPriority() { return priority; }
        public void setPriority(Email.EmailPriority priority) { this.priority = priority; }
    }
    
    public static class CaseResolution {
        private String resolutionNotes;
        private Case.CaseStatus finalStatus;
        
        // Getters and Setters
        public String getResolutionNotes() { return resolutionNotes; }
        public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
        
        public Case.CaseStatus getFinalStatus() { return finalStatus; }
        public void setFinalStatus(Case.CaseStatus finalStatus) { this.finalStatus = finalStatus; }
    }
    
    public static class CaseUpdate {
        private String notes;
        private Case.CasePriority priority;
        private Case.CaseStatus status;
        
        // Getters and Setters
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        public Case.CasePriority getPriority() { return priority; }
        public void setPriority(Case.CasePriority priority) { this.priority = priority; }
        
        public Case.CaseStatus getStatus() { return status; }
        public void setStatus(Case.CaseStatus status) { this.status = status; }
    }
    
    public static class AgentWorkload {
        private String agentId;
        private int currentCases;
        private int maxCases;
        private List<Case> assignedCases;
        
        public AgentWorkload(String agentId, int currentCases, int maxCases, List<Case> assignedCases) {
            this.agentId = agentId;
            this.currentCases = currentCases;
            this.maxCases = maxCases;
            this.assignedCases = assignedCases;
        }
        
        // Getters and Setters
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        
        public int getCurrentCases() { return currentCases; }
        public void setCurrentCases(int currentCases) { this.currentCases = currentCases; }
        
        public int getMaxCases() { return maxCases; }
        public void setMaxCases(int maxCases) { this.maxCases = maxCases; }
        
        public List<Case> getAssignedCases() { return assignedCases; }
        public void setAssignedCases(List<Case> assignedCases) { this.assignedCases = assignedCases; }
    }
}