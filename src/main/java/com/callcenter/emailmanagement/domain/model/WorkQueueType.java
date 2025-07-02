package com.callcenter.emailmanagement.domain.model;

public enum WorkQueueType {
    GENERAL_INQUIRY("General Inquiry", 1),
    BILLING_SUPPORT("Billing Support", 2);
    
    private final String displayName;
    private final int priority;
    
    WorkQueueType(String displayName, int priority) {
        this.displayName = displayName;
        this.priority = priority;
    }
    
    public String getDisplayName() { return displayName; }
    public int getPriority() { return priority; }
}