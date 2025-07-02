package com.callcenter.emailmanagement.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_queue")
public class WorkQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseItem;
    
    @Enumerated(EnumType.STRING)
    private WorkQueueType queueType;
    
    @Enumerated(EnumType.STRING)
    private QueueStatus status;
    
    private Integer priorityScore;
    
    private LocalDateTime addedToQueueTime;
    
    private LocalDateTime assignedTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    private Agent assignedAgent;

    public enum QueueStatus {
        PENDING, ASSIGNED, COMPLETED
    }

    // Constructors
    public WorkQueue() {
        this.addedToQueueTime = LocalDateTime.now();
        this.status = QueueStatus.PENDING;
    }
    
    public WorkQueue(Case caseItem, WorkQueueType queueType) {
        this();
        this.caseItem = caseItem;
        this.queueType = queueType;
        this.priorityScore = calculatePriorityScore();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Case getCaseItem() { return caseItem; }
    public void setCaseItem(Case caseItem) { this.caseItem = caseItem; }
    
    public WorkQueueType getQueueType() { return queueType; }
    public void setQueueType(WorkQueueType queueType) { this.queueType = queueType; }
    
    public QueueStatus getStatus() { return status; }
    public void setStatus(QueueStatus status) { 
        this.status = status;
        if (status == QueueStatus.ASSIGNED) {
            this.assignedTime = LocalDateTime.now();
        }
    }
    
    public Integer getPriorityScore() { return priorityScore; }
    public void setPriorityScore(Integer priorityScore) { this.priorityScore = priorityScore; }
    
    public LocalDateTime getAddedToQueueTime() { return addedToQueueTime; }
    public void setAddedToQueueTime(LocalDateTime addedToQueueTime) { this.addedToQueueTime = addedToQueueTime; }
    
    public LocalDateTime getAssignedTime() { return assignedTime; }
    public void setAssignedTime(LocalDateTime assignedTime) { this.assignedTime = assignedTime; }
    
    public Agent getAssignedAgent() { return assignedAgent; }
    public void setAssignedAgent(Agent assignedAgent) { this.assignedAgent = assignedAgent; }
    
    private Integer calculatePriorityScore() {
        int score = 0;
        
        if (caseItem != null) {
            switch (caseItem.getPriority()) {
                case URGENT: score += 100; break;
                case HIGH: score += 75; break;
                case NORMAL: score += 50; break;
                case LOW: score += 25; break;
            }
        }
        
        if (queueType != null) {
            score += queueType.getPriority() * 10;
        }
        
        return score;
    }
}