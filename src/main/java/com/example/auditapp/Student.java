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

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getDepartment() { return department; }
    public String getEmail() { return email; }

    public void submitLeave(LeaveForm leave, List<LeaveForm> database) {
        database.add(leave);
        System.out.println("學生 " + this.studentName + " (" + this.department + ") 已提交假單：" + leave.getId());
    }
}