package com.example.auditapp;

public class StudentLeaveForm {
    private String id;
    private String studentName;
    private String studentId;
    private String type; // 病假, 事假
    private String date;
    private String reason;
    private String status; // 待審核, 通過, 不通過
    private String email;

public StudentLeaveForm(String id, String studentName, String studentId, String type, String date, String reason, String status, String email) {
        this.id = id;
        this.studentName = studentName;
        this.studentId = studentId;
        this.type = type;
        this.date = date;
        this.reason = reason;
        this.status = status;
        this.email = email;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStudentName() { return studentName; }
    public String getStudentId() { return studentId; }
    public String getType() { return type; }
    public String getDate() { return date; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}