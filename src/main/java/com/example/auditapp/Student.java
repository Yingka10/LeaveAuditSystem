package com.example.auditapp;

import java.util.List;

public class Student {
    private String studentId;
    private String studentName;
    private String department;
    private String email;

    public Student(String studentId, String studentName, String department, String email) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.department = department;
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getDepartment() {
        return department;
    }

    public String getEmail() {
        return email;
    }

    public void submitLeave(LeaveForm leave, List<LeaveForm> database) {
        database.add(leave);
        System.out.println("學生 " + this.studentName + " (" + this.department + ") 已提交假單：" + leave.getId());
    }

    public void modifyLeave(LeaveForm leave, String newType, String newReason, String newStart, String newEnd,
            String newEvidence, String newConsent) {
        if (leave.getStudentId().equals(this.studentId)) {
            leave.renewInfo(newType, newReason, newStart, newEnd, newEvidence, newConsent);
            System.out.println("學生 " + this.studentName + " 已修正並重送假單 (ID: " + leave.getId() + ")");
        } else {
            System.out.println("錯誤：無法修改非本人的假單。");
        }
    }

}