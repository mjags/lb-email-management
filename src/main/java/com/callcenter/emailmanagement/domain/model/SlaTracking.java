package com.callcenter.emailmanagement.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "sla_tracking")
public class SlaTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;
    
    private LocalDateTime firstResponseTime;
    
    private LocalDateTime resolutionTime;
    
    private Long firstResponseTimeMinutes;
    
    private Long resolutionTimeMinutes;
    
    private Integer slaTarget24Hours = 24 * 60; // 24 hours in minutes
    
    private Integer slaTarget48Hours = 48 * 60; // 48 hours in minutes
    
    private Boolean firstResponseSlaMet;
    
    private Boolean resolutionSlaMet;
    
    @Enumerated(EnumType.STRING)
    private SlaStatus slaStatus;

    public enum SlaStatus {
        WITHIN_SLA, APPROACHING_BREACH, BREACHED
    }

    // Constructors
    public SlaTracking() {}
    
    public SlaTracking(Case caseEntity) {
        this.caseEntity = caseEntity;
        this.slaStatus = SlaStatus.WITHIN_SLA;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }
    
    public LocalDateTime getFirstResponseTime() { return firstResponseTime; }
    public void setFirstResponseTime(LocalDateTime firstResponseTime) { 
        this.firstResponseTime = firstResponseTime;
        calculateFirstResponseTime();
    }
    
    public LocalDateTime getResolutionTime() { return resolutionTime; }
    public void setResolutionTime(LocalDateTime resolutionTime) { 
        this.resolutionTime = resolutionTime;
        calculateResolutionTime();
    }
    
    public Long getFirstResponseTimeMinutes() { return firstResponseTimeMinutes; }
    public void setFirstResponseTimeMinutes(Long firstResponseTimeMinutes) { 
        this.firstResponseTimeMinutes = firstResponseTimeMinutes; 
    }
    
    public Long getResolutionTimeMinutes() { return resolutionTimeMinutes; }
    public void setResolutionTimeMinutes(Long resolutionTimeMinutes) { 
        this.resolutionTimeMinutes = resolutionTimeMinutes; 
    }
    
    public Integer getSlaTarget24Hours() { return slaTarget24Hours; }
    public void setSlaTarget24Hours(Integer slaTarget24Hours) { this.slaTarget24Hours = slaTarget24Hours; }
    
    public Integer getSlaTarget48Hours() { return slaTarget48Hours; }
    public void setSlaTarget48Hours(Integer slaTarget48Hours) { this.slaTarget48Hours = slaTarget48Hours; }
    
    public Boolean getFirstResponseSlaMet() { return firstResponseSlaMet; }
    public void setFirstResponseSlaMet(Boolean firstResponseSlaMet) { this.firstResponseSlaMet = firstResponseSlaMet; }
    
    public Boolean getResolutionSlaMet() { return resolutionSlaMet; }
    public void setResolutionSlaMet(Boolean resolutionSlaMet) { this.resolutionSlaMet = resolutionSlaMet; }
    
    public SlaStatus getSlaStatus() { return slaStatus; }
    public void setSlaStatus(SlaStatus slaStatus) { this.slaStatus = slaStatus; }
    
    private void calculateFirstResponseTime() {
        if (firstResponseTime != null && caseEntity != null) {
            firstResponseTimeMinutes = ChronoUnit.MINUTES.between(caseEntity.getCreatedDate(), firstResponseTime);
            firstResponseSlaMet = firstResponseTimeMinutes <= slaTarget24Hours;
        }
    }
    
    private void calculateResolutionTime() {
        if (resolutionTime != null && caseEntity != null) {
            resolutionTimeMinutes = ChronoUnit.MINUTES.between(caseEntity.getCreatedDate(), resolutionTime);
            resolutionSlaMet = resolutionTimeMinutes <= slaTarget48Hours;
        }
    }
    
    public void updateSlaStatus() {
        if (caseEntity == null) return;
        
        LocalDateTime now = LocalDateTime.now();
        long minutesElapsed = ChronoUnit.MINUTES.between(caseEntity.getCreatedDate(), now);
        
        if (firstResponseTime == null) {
            if (minutesElapsed >= slaTarget24Hours) {
                slaStatus = SlaStatus.BREACHED;
            } else if (minutesElapsed >= slaTarget24Hours * 0.8) {
                slaStatus = SlaStatus.APPROACHING_BREACH;
            }
        } else if (resolutionTime == null) {
            if (minutesElapsed >= slaTarget48Hours) {
                slaStatus = SlaStatus.BREACHED;
            } else if (minutesElapsed >= slaTarget48Hours * 0.8) {
                slaStatus = SlaStatus.APPROACHING_BREACH;
            }
        } else {
            slaStatus = SlaStatus.WITHIN_SLA;
        }
    }
}