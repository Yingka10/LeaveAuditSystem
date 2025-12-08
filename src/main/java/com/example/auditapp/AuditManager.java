package com.example.auditapp;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AuditManager {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    private static List<LeaveForm> leaveDb = new ArrayList<>();
    private static List<AuditRecord> auditRecords = new ArrayList<>();

    private static Teacher currentTeacher = new Teacher("T999", "陳導師", "yourgmail@gmail.com");
    private static Student demoStudent = new Student("112403508", "周佳穎", "student@example.com");

    // 初始化假資料
    static {
        // 先建立學生實體
        Student s1 = new Student("112403508", "周佳穎", "test1@example.com");
        Student s2 = new Student("112400001", "王小明", "test2@example.com");

        String demoFileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";
        String demoImgUrl = "https://via.placeholder.com/600x400.png?text=Proof+Document";

        List<LeaveForm.ScheduleItem> sch1 = new ArrayList<>();
        sch1.add(new LeaveForm.ScheduleItem("2025-11-25", "星期二", "5", "排球", "陳政達"));
        
        // --- 修改：建構子傳入 Student 物件 ---
        leaveDb.add(new LeaveForm("L001", s1, "事假", "需返鄉", "待審核", sch1, null, demoFileUrl));

        List<LeaveForm.ScheduleItem> sch2 = new ArrayList<>();
        sch2.add(new LeaveForm.ScheduleItem("2025-12-02", "星期二", "3", "微積分", "林數學"));
        
        leaveDb.add(new LeaveForm("L002", s2, "病假", "發燒", "待審核", sch2, demoImgUrl, demoFileUrl));
    }

    // --- 演示觸發區 ---
    @GetMapping("/demo")
    public String demoStartPage() { return "demo"; }

    @PostMapping("/demo/trigger")
    public String triggerStudentSubmission(RedirectAttributes redirectAttributes) {
        List<LeaveForm.ScheduleItem> schedule = new ArrayList<>();
        schedule.add(new LeaveForm.ScheduleItem("2025-12-10", "星期三", "1", "系統分析", "王老師"));
        
        // --- 修改：建構子傳入 demoStudent 物件 ---
        LeaveForm newLeave = new LeaveForm(
            "L" + System.currentTimeMillis(),
            demoStudent, // 直接傳入物件
            "事假",
            "家中急事需請假",
            "待審核",
            schedule,
            "https://example.com/proof.pdf",
            "https://example.com/consent.pdf"
        );

        demoStudent.submitLeave(newLeave, leaveDb);

        String loginLink = "http://localhost:8080/";
        String subject = "【系統通知】您有一筆新的請假審核申請";
        String text = "親愛的 " + currentTeacher.getTeacherName() + " 您好：\n\n" +
                      "學生 " + demoStudent.getStudentName() + " 已提交新的請假申請。\n" +
                      "請點擊下方連結進入系統進行審核：\n" + loginLink;
        
        sendEmailSafely(currentTeacher.getEmail(), subject, text);
        currentTeacher.receiveNotification(subject);

        redirectAttributes.addFlashAttribute("message", "模擬成功！已通知導師 " + currentTeacher.getTeacherName());
        return "redirect:/demo";
    }

    // --- 系統業務流程 (Login, Logout, List, Detail) 保持不變 ---
    @GetMapping("/") public String loginPage() { return "login"; }
    @PostMapping("/login") public String doLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        if ("teacher".equals(username) && "1234".equals(password)) {
            session.setAttribute("loggedInUser", currentTeacher.getTeacherName());
            return "redirect:/leaves";
        }
        model.addAttribute("error", "帳號或密碼錯誤");
        return "login";
    }
    @GetMapping("/logout") public String logout(HttpSession session) { session.invalidate(); return "redirect:/"; }
    @GetMapping("/leaves") public String listLeaves(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/";
        model.addAttribute("leaves", leaveDb);
        return "list";
    }
    @GetMapping("/leaves/{id}") public String leaveDetail(@PathVariable String id, HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/";
        LeaveForm leave = leaveDb.stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null);
        model.addAttribute("leave", leave);
        return "detail";
    }
    @GetMapping("/audit-logs") public String viewAuditLogs(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/";
        model.addAttribute("logs", auditRecords);
        return "logs";
    }

    // --- 審核流程 (更新) ---
    @PostMapping("/leaves/{id}/audit")
    public String auditSingle(@PathVariable String id, 
                              @RequestParam String action,
                              @RequestParam(required = false) String reason,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/";

        LeaveForm leave = leaveDb.stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null);
        
        if (leave != null) {
            processAudit(leave, action, reason);
            if ("approve".equals(action)) {
                redirectAttributes.addFlashAttribute("message", "單號 " + id + " 已核准。");
            } else {
                redirectAttributes.addFlashAttribute("message", "單號 " + id + " 已駁回，並寄信通知學生。");
            }
        }
        return "redirect:/leaves";
    }

    private void processAudit(LeaveForm leave, String action, String reason) {
        String resultStatus = "";
        String logAction = "";
        String operatorName = currentTeacher.getTeacherName();
        
        // --- 修改：透過 leave.getStudent() 取得姓名 ---
        String studentName = leave.getStudent().getStudentName();

        if ("approve".equals(action)) {
            resultStatus = "通過";
            logAction = "核准";
            leave.setStatus(resultStatus);
            // --- 修改：使用 rejectedReason 欄位 (通過時可為 null 或 "無") ---
            auditRecords.add(0, new AuditRecord(leave.getId(), studentName, logAction, null, operatorName));
        } else if ("reject".equals(action)) {
            resultStatus = "不通過";
            logAction = "駁回";
            leave.setStatus(resultStatus);
            // --- 修改：將原因填入 rejectedReason ---
            auditRecords.add(0, new AuditRecord(leave.getId(), studentName, logAction, reason, operatorName));
            
            // 寄信給學生
            String subject = "【假單通知】您的假單未核准 - 請修正後重新申請";
            String body = "親愛的 " + studentName + " 同學：\n\n" +
                          "您的假單 (單號: " + leave.getId() + ") 未通過審核。\n" +
                          "駁回原因：" + reason + "\n\n" +
                          "導師 " + operatorName;
            
            // --- 修改：透過 leave.getStudent().getEmail() 取得 Email ---
            sendEmailSafely(leave.getStudent().getEmail(), subject, body);
        }
    }

    private void sendEmailSafely(String to, String subject, String text) {
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(text);
                message.setFrom("your-email@gmail.com"); 
                mailSender.send(message);
                System.out.println("Email Sent to: " + to);
            } else {
                System.out.println(">> 模擬寄信: To " + to + " | Subject: " + subject);
            }
        } catch (Exception e) {
            System.err.println("Email Failed: " + e.getMessage());
        }
    }
}