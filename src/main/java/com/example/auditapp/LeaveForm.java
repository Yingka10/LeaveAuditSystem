package com.example.auditapp;

public class LeaveForm {
    
    private String id;
    private String studentId;
    private String leaveType;
    private String reason;
    private String status;
    private String startTime;
    private String endTime;
    private String evidenceFile;
    private String teacherConsentFile;

    public LeaveForm(String id, String studentId, String leaveType,
            String reason, String status,
            String startTime, String endTime,
            String evidenceFile, String teacherConsentFile) {
        this.id = id;
        this.studentId = studentId;
        this.leaveType = leaveType;
        this.reason = reason;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.evidenceFile = evidenceFile;
        this.teacherConsentFile = teacherConsentFile;
    }

    public void renewInfo(String leaveType, String reason, String startTime, String endTime, String evidenceFile,
            String teacherConsentFile) {
        this.leaveType = leaveType;
        this.reason = reason;
        this.startTime = startTime;
        this.endTime = endTime;

        this.evidenceFile = evidenceFile;
        this.teacherConsentFile = teacherConsentFile;

        this.status = "待審核";
        System.out.println("假單 " + this.id + " 內容已更新，狀態重置為待審核。");
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getEvidenceFile() {
        return evidenceFile;
    }

    public String getTeacherConsentFile() {
        return teacherConsentFile;
    }

    public String getDateSummary() {
        return startTime + " 至 " + endTime;
    }

    //根據student的id跟auditmanager取得student的資料
    public Student getStudent() {
        return AuditManager.getStudentById(this.studentId);
    }
}