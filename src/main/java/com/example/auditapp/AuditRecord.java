package com.example.auditapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditRecord {
    private String leaveId;
    private String studentName;
    private String action; 
    
    // --- 修改重點：改名為 rejectedReason ---
    private String rejectedReason; 
    
    private String operator;
    private String timestamp;

    public AuditRecord(String leaveId, String studentName, String action, String rejectedReason, String operator) {
        this.leaveId = leaveId;
        this.studentName = studentName;
        this.action = action;
        this.rejectedReason = rejectedReason;
        this.operator = operator;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Getters
    public String getLeaveId() { return leaveId; }
    public String getStudentName() { return studentName; }
    public String getAction() { return action; }
    
    // --- Getter 也要改名 ---
    public String getRejectedReason() { return rejectedReason; }
    
    public String getOperator() { return operator; }
    public String getTimestamp() { return timestamp; }
}