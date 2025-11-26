package com.example.auditapp;

import java.util.List;

public class StudentLeaveForm {
    private String id;
    private String studentName;
    private String studentId;
    private String type; 
    private String reason; 
    private String status;
    private String email;
    
    // 請假節次清單
    private List<ScheduleItem> schedule;
    
    // 證明文件 (選填)
    private String evidenceFile; 
    
    // 已預先向任課教師請假證明 (必填)
    private String teacherConsentFile;

    // --- 內部類別：定義每一節課的結構 ---
    public static class ScheduleItem {
        public String date;    
        public String day;     
        public String period;  
        public String course;  
        public String teacher; 

        public ScheduleItem(String date, String day, String period, String course, String teacher) {
            this.date = date;
            this.day = day;
            this.period = period;
            this.course = course;
            this.teacher = teacher;
        }
    }

    public StudentLeaveForm(String id, String studentName, String studentId, String type, 
                          String reason, String status, String email, 
                          List<ScheduleItem> schedule, String evidenceFile, String teacherConsentFile) {
        this.id = id;
        this.studentName = studentName;
        this.studentId = studentId;
        this.type = type;
        this.reason = reason;
        this.status = status;
        this.email = email;
        this.schedule = schedule;
        this.evidenceFile = evidenceFile;
        this.teacherConsentFile = teacherConsentFile;
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public String getStudentName() { return studentName; }
    public String getStudentId() { return studentId; }
    public String getType() { return type; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEmail() { return email; }
    public List<ScheduleItem> getSchedule() { return schedule; }
    public String getEvidenceFile() { return evidenceFile; }
    public String getTeacherConsentFile() { return teacherConsentFile; }
    public String getDateSummary() {
        if (schedule != null && !schedule.isEmpty()) {
            return schedule.get(0).date + " (" + schedule.size() + "節)";
        }
        return "無日期";
    }
}