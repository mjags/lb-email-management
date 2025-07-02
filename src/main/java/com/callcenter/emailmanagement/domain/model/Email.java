package com.callcenter.emailmanagement.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "emails")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String messageId;
    
    @Column(nullable = false)
    private String subject;
    
    @Column(nullable = false)
    private String fromAddress;
    
    @Column(nullable = false)
    private String toAddress;
    
    @ElementCollection
    private List<String> ccAddresses;
    
    @Lob
    private String htmlContent;
    
    @Lob
    private String textContent;
    
    @ElementCollection
    private List<String> attachmentUrls;
    
    private LocalDateTime receivedDate;
    
    private LocalDateTime sentDate;
    
    @Enumerated(EnumType.STRING)
    private EmailDirection direction;
    
    @Enumerated(EnumType.STRING)
    private EmailPriority priority;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private Case emailCase;

    public enum EmailDirection {
        INBOUND, OUTBOUND
    }
    
    public enum EmailPriority {
        LOW, NORMAL, HIGH, URGENT
    }

    // Constructors
    public Email() {}
    
    public Email(String messageId, String subject, String fromAddress, String toAddress) {
        this.messageId = messageId;
        this.subject = subject;    
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.receivedDate = LocalDateTime.now();
        this.priority = EmailPriority.NORMAL;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getFromAddress() { return fromAddress; }
    public void setFromAddress(String fromAddress) { this.fromAddress = fromAddress; }
    
    public String getToAddress() { return toAddress; }
    public void setToAddress(String toAddress) { this.toAddress = toAddress; }
    
    public List<String> getCcAddresses() { return ccAddresses; }
    public void setCcAddresses(List<String> ccAddresses) { this.ccAddresses = ccAddresses; }
    
    public String getHtmlContent() { return htmlContent; }
    public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }
    
    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }
    
    public List<String> getAttachmentUrls() { return attachmentUrls; }
    public void setAttachmentUrls(List<String> attachmentUrls) { this.attachmentUrls = attachmentUrls; }
    
    public LocalDateTime getReceivedDate() { return receivedDate; }
    public void setReceivedDate(LocalDateTime receivedDate) { this.receivedDate = receivedDate; }
    
    public LocalDateTime getSentDate() { return sentDate; }
    public void setSentDate(LocalDateTime sentDate) { this.sentDate = sentDate; }
    
    public EmailDirection getDirection() { return direction; }
    public void setDirection(EmailDirection direction) { this.direction = direction; }
    
    public EmailPriority getPriority() { return priority; }
    public void setPriority(EmailPriority priority) { this.priority = priority; }
    
    public Case getEmailCase() { return emailCase; }
    public void setEmailCase(Case emailCase) { this.emailCase = emailCase; }
}