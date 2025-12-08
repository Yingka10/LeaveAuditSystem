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

    private static Teacher currentTeacher = new Teacher("T999", "陳導師", "your-email@gmail.com");
    private static Student demoStudent = new Student("112403508", "周佳穎", "資管系", "student@example.com"); // 加入系所

    // 初始化假資料
    static {
        Student s1 = new Student("112403508", "周佳穎", "資管系", "test1@example.com");
        Student s2 = new Student("112400001", "王小明", "資工系", "test2@example.com");

        String demoFileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";
        String demoImgUrl = "https://via.placeholder.com/600x400.png?text=Proof+Document";

        leaveDb.add(new LeaveForm("L001", s1, "事假", "需返鄉", "待審核", "2025-11-25 13:00", "2025-11-25 15:00", null, demoFileUrl));
        leaveDb.add(new LeaveForm("L002", s2, "病假", "發燒", "待審核", "2025-12-02 09:00", "2025-12-02 12:00", demoImgUrl, demoFileUrl));
    }

    // --- 演示觸發區 ---
    @GetMapping("/demo")
    public String demoStartPage() { return "demo"; }

    @PostMapping("/demo/trigger")
    public String triggerStudentSubmission(RedirectAttributes redirectAttributes) {
        LeaveForm newLeave = new LeaveForm(
            "L" + System.currentTimeMillis(),
            demoStudent,
            "事假",
            "家中急事需請假",
            "待審核",
            "2025-12-10 08:00",
            "2025-12-10 10:00",
            "https://example.com/proof.pdf",
            "https://example.com/consent.pdf"
        );

        demoStudent.submitLeave(newLeave, leaveDb);

        String subject = "【系統通知】您有一筆新的請假審核申請";
        String text = "親愛的 " + currentTeacher.getTeacherName() + " 您好：\n\n" +
                      "學生 " + demoStudent.getStudentName() + " (" + demoStudent.getDepartment() + ") 已提交新的請假申請。\n" +
                      "請點擊下方連結進入系統進行審核：\nhttp://localhost:8080/";
        
        sendEmailSafely(currentTeacher.getEmail(), subject, text);
        currentTeacher.receiveNotification(subject);

        redirectAttributes.addFlashAttribute("message", "模擬成功！學生 (" + demoStudent.getDepartment() + ") 已提交假單。");
        return "redirect:/demo";
    }

    // --- 系統業務流程 ---
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
    
    @GetMapping("/leaves") 
    public String listLeaves(HttpSession session, Model model) {
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
        String resultRecord = ""; 
        String advisorId = currentTeacher.getTeacherId();

        if ("approve".equals(action)) {
            resultStatus = "通過";
            resultRecord = "核准";
            leave.setStatus(resultStatus);
            // 建立紀錄 (Create audit)
            auditRecords.add(0, new AuditRecord(leave.getId(), leave.getStudent().getStudentName(), resultRecord, null, advisorId));
        } else if ("reject".equals(action)) {
            resultStatus = "不通過";
            resultRecord = "駁回";
            leave.setStatus(resultStatus);
            // 建立紀錄 (Create audit)
            auditRecords.add(0, new AuditRecord(leave.getId(), leave.getStudent().getStudentName(), resultRecord, reason, advisorId));
            
            // 寄信 (Send email)
            String subject = "【假單通知】您的假單未核准";
            String body = "親愛的同學：您的假單 (" + leave.getId() + ") 未通過。原因：" + reason;
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
                System.out.println(">> 模擬寄信: To " + to);
            }
        } catch (Exception e) {
            System.err.println("Email Failed: " + e.getMessage());
        }
    }
}