package com.example.auditapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditRecord {
    private String leaveId;
    private String result;        
    private String rejectReason;  
    private String advisorId;     
    private String timestamp;

    private String studentName; 

    public AuditRecord(String leaveId, String studentName, String result, String rejectReason, String advisorId) {
        this.leaveId = leaveId;
        this.studentName = studentName;
        this.result = result;
        this.rejectReason = rejectReason;
        this.advisorId = advisorId;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Getters 更新
    public String getLeaveId() { return leaveId; }
    public String getStudentName() { return studentName; }
    public String getResult() { return result; }         
    public String getRejectReason() { return rejectReason; } 
    public String getAdvisorId() { return advisorId; }   
    public String getTimestamp() { return timestamp; }
}