package com.callcenter.emailmanagement.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "agents")
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String agentId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Enumerated(EnumType.STRING)
    private AgentStatus status;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<WorkQueueType> skillSets;
    
    private LocalDateTime lastActiveTime;
    
    private Integer maxConcurrentCases = 5;
    
    private Integer currentCaseCount = 0;

    public enum AgentStatus {
        AVAILABLE, BUSY, OFFLINE, BREAK
    }

    // Constructors
    public Agent() {}
    
    public Agent(String agentId, String name, String email) {
        this.agentId = agentId;
        this.name = name;
        this.email = email;
        this.status = AgentStatus.OFFLINE;
        this.lastActiveTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public AgentStatus getStatus() { return status; }
    public void setStatus(AgentStatus status) { this.status = status; }
    
    public List<WorkQueueType> getSkillSets() { return skillSets; }
    public void setSkillSets(List<WorkQueueType> skillSets) { this.skillSets = skillSets; }
    
    public LocalDateTime getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(LocalDateTime lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    
    public Integer getMaxConcurrentCases() { return maxConcurrentCases; }
    public void setMaxConcurrentCases(Integer maxConcurrentCases) { this.maxConcurrentCases = maxConcurrentCases; }
    
    public Integer getCurrentCaseCount() { return currentCaseCount; }
    public void setCurrentCaseCount(Integer currentCaseCount) { this.currentCaseCount = currentCaseCount; }
    
    public boolean isAvailable() {
        return status == AgentStatus.AVAILABLE && currentCaseCount < maxConcurrentCases;
    }
}