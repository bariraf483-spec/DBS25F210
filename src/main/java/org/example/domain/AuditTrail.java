package org.example.domain;

import java.sql.Timestamp;

public class AuditTrail {
    private int auditId;
    private String tableName;
    private String actionType;
    private String details;
    private Timestamp actionTimestamp;

    // Default Constructor
    public AuditTrail() {}

    // Parameterized Constructor
    public AuditTrail(int auditId, String tableName, String actionType, String details, Timestamp actionTimestamp) {
        this.auditId = auditId;
        this.tableName = tableName;
        this.actionType = actionType;
        this.details = details;
        this.actionTimestamp = actionTimestamp;
    }

    // Getters and Setters
    public int getAuditId() { return auditId; }
    public void setAuditId(int auditId) { this.auditId = auditId; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Timestamp getActionTimestamp() { return actionTimestamp; }
    public void setActionTimestamp(Timestamp actionTimestamp) { this.actionTimestamp = actionTimestamp; }
}