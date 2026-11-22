package com.sk.markethub.model;

public class AuditLog {
    private Long id;
    private Long userId;
    private String action;
    private String entityType;
    private Long entityId;
    private String ipAddress;
    private String createdAt;

    public AuditLog() {}

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getAction() { return action; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public String getIpAddress() { return ipAddress; }
    public String getCreatedAt() { return createdAt; }
}
