package com.example.auditapp;

public class Teacher {
    private String teacherId;
    private String teacherName;
    private String email;

    public Teacher(String teacherId, String teacherName, String email) {
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.email = email;
    }

    public String getTeacherId() { return teacherId; }
    public String getTeacherName() { return teacherName; }
    public String getEmail() { return email; 

    }
}