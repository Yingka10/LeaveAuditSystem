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
    public String getEmail() { return email; }


    public void receiveNotification(String subject) {
        System.out.println("【模擬】導師 " + this.teacherName + " (" + this.email + ") 已收到 Email 通知：[" + subject + "]");
        System.out.println(" -> 導師點擊信中連結，準備進入系統...");
    }
}