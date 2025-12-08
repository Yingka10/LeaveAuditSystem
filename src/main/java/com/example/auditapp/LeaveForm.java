package com.example.auditapp;

public class LeaveForm { 
    private String id;
    private Student student; 
    private String studentId; 
    
    private String leaveType;
    private String reason;
    private String status;
    
    private String startTime;
    private String endTime;
    
    private String evidenceFile;
    private String teacherConsentFile;

    public LeaveForm(String id, Student student, String leaveType, 
                          String reason, String status, 
                          String startTime, String endTime, 
                          String evidenceFile, String teacherConsentFile) {
        this.id = id;
        this.student = student;
        this.studentId = student.getStudentId();
        this.leaveType = leaveType;
        this.reason = reason;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.evidenceFile = evidenceFile;
        this.teacherConsentFile = teacherConsentFile;
    }

    // Getters
    public String getId() { return id; }
    public Student getStudent() { return student; }
    public String getStudentId() { return studentId; }
    
    public String getLeaveType() { return leaveType; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    
    public String getEvidenceFile() { return evidenceFile; }
    public String getTeacherConsentFile() { return teacherConsentFile; }
    
    public String getDateSummary() {
        return startTime + " 至 " + endTime;
    }
}