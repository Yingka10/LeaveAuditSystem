package com.example.auditapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLog {
    private String leaveId;
    private String studentName;
    private String action; // "核准" 或 "駁回"
    private String operator; // 操作者 (Teacher)
    private String timestamp;

    public AuditLog(String leaveId, String studentName, String action, String operator) {
        this.leaveId = leaveId;
        this.studentName = studentName;
        this.action = action;
        this.operator = operator;
        // 自動抓取現在時間
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Getters
    public String getLeaveId() { return leaveId; }
    public String getStudentName() { return studentName; }
    public String getAction() { return action; }
    public String getOperator() { return operator; }
    public String getTimestamp() { return timestamp; }
}