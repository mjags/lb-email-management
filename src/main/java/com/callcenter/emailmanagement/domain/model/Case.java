package com.callcenter.emailmanagement.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cases")
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String caseNumber;
    
    @Column(nullable = false)
    private String customerEmail;
    
    private String customerName;
    
    @Column(nullable = false)
    private String subject;
    
    @Lob
    private String description;
    
    @Enumerated(EnumType.STRING)
    private CaseStatus status;
    
    @Enumerated(EnumType.STRING)    
    private CasePriority priority;
    
    @Enumerated(EnumType.STRING)
    private WorkQueueType queueType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    private Agent assignedAgent;
    
    @OneToMany(mappedBy = "emailCase", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Email> emails = new ArrayList<>();
    
    @OneToOne(mappedBy = "caseEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SlaTracking slaTracking;
    
    private LocalDateTime createdDate;
    
    private LocalDateTime lastModifiedDate;
    
    private LocalDateTime resolvedDate;

    public enum CaseStatus {
        NEW, ASSIGNED, IN_PROGRESS, PENDING_CUSTOMER, RESOLVED, CLOSED
    }
    
    public enum CasePriority {
        LOW, NORMAL, HIGH, URGENT
    }

    // Constructors
    public Case() {
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        this.status = CaseStatus.NEW;
        this.priority = CasePriority.NORMAL;
    }
    
    public Case(String caseNumber, String customerEmail, String subject) {
        this();
        this.caseNumber = caseNumber;
        this.customerEmail = customerEmail;
        this.subject = subject;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public CaseStatus getStatus() { return status; }
    public void setStatus(CaseStatus status) { 
        this.status = status;
        this.lastModifiedDate = LocalDateTime.now();
        if (status == CaseStatus.RESOLVED || status == CaseStatus.CLOSED) {
            this.resolvedDate = LocalDateTime.now();
        }
    }
    
    public CasePriority getPriority() { return priority; }
    public void setPriority(CasePriority priority) { this.priority = priority; }
    
    public WorkQueueType getQueueType() { return queueType; }
    public void setQueueType(WorkQueueType queueType) { this.queueType = queueType; }
    
    public Agent getAssignedAgent() { return assignedAgent; }
    public void setAssignedAgent(Agent assignedAgent) { this.assignedAgent = assignedAgent; }
    
    public List<Email> getEmails() { return emails; }
    public void setEmails(List<Email> emails) { this.emails = emails; }
    
    public SlaTracking getSlaTracking() { return slaTracking; }
    public void setSlaTracking(SlaTracking slaTracking) { this.slaTracking = slaTracking; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }
    
    public LocalDateTime getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(LocalDateTime resolvedDate) { this.resolvedDate = resolvedDate; }
    
    public void addEmail(Email email) {
        emails.add(email);
        email.setEmailCase(this);
        this.lastModifiedDate = LocalDateTime.now();
    }
}