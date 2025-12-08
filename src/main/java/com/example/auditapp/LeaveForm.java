package com.example.auditapp;

import java.util.List;

public class LeaveForm { 
    private String id;
    
    private Student student; 
    
    private String studentId; 
    private String type;
    private String reason; 
    private String status;
    private List<ScheduleItem> schedule;
    private String evidenceFile;
    private String teacherConsentFile;

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

    public LeaveForm(String id, Student student, String type, 
                          String reason, String status, 
                          List<ScheduleItem> schedule, String evidenceFile, String teacherConsentFile) {
        this.id = id;
        this.student = student;
        this.studentId = student.getStudentId(); 
        this.type = type;
        this.reason = reason;
        this.status = status;
        this.schedule = schedule;
        this.evidenceFile = evidenceFile;
        this.teacherConsentFile = teacherConsentFile;
    }

    // Getters
    public String getId() { return id; }
    

    public Student getStudent() { return student; }
    
    public String getStudentId() { return studentId; }
    public String getType() { return type; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
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