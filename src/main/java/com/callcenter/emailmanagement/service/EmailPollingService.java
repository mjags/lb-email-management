package com.callcenter.emailmanagement.service;

import com.callcenter.emailmanagement.domain.model.Email;
import com.callcenter.emailmanagement.domain.model.Case;
import com.callcenter.emailmanagement.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class EmailPollingService {
    private static final Logger logger = LoggerFactory.getLogger(EmailPollingService.class);
    
    @Autowired
    private OutlookEmailService outlookEmailService;
    
    @Autowired
    private EmailRepository emailRepository;
    
    @Autowired
    private CaseManagementService caseManagementService;
    
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void pollForNewEmails() {
        logger.info("🔍 Polling for new emails...");
        
        try {
            List<Email> newEmails = outlookEmailService.fetchEmailsFromFolder("INBOX");
            
            if (newEmails.isEmpty()) {
                logger.debug("📭 No new emails found");
                return;
            }
            
            logger.info("📧 Found {} new emails to process", newEmails.size());
            
            for (Email email : newEmails) {
                processNewEmail(email);
            }
            
        } catch (Exception e) {
            logger.error("❌ Error during email polling: {}", e.getMessage(), e);
        }
    }
    
    private void processNewEmail(Email email) {
        logger.info("📨 Processing new email: '{}' from {}", email.getSubject(), email.getFromAddress());
        
        try {
            // Check if email already exists
            if (emailRepository.findByMessageId(email.getMessageId()).isPresent()) {
                logger.debug("📧 Email already processed: {}", email.getMessageId());
                return;
            }
            
            // Save email first
            Email savedEmail = emailRepository.save(email);
            logger.info("💾 Email saved to database: {}", savedEmail.getMessageId());
            
            // Create case from email
            Case newCase = caseManagementService.createCaseFromEmail(savedEmail);
            logger.info("📋 Case created: {} for email: {}", newCase.getCaseNumber(), savedEmail.getSubject());
            
            // Log the complete flow
            logger.info("✅ Email processing completed:");
            logger.info("   📧 Email: {} (ID: {})", savedEmail.getSubject(), savedEmail.getMessageId());
            logger.info("   📋 Case: {} (Status: {})", newCase.getCaseNumber(), newCase.getStatus());
            logger.info("   🎯 Queue: {} (Priority: {})", newCase.getQueueType(), newCase.getPriority());
            
        } catch (Exception e) {
            logger.error("❌ Error processing email {}: {}", email.getSubject(), e.getMessage(), e);
        }
    }
}