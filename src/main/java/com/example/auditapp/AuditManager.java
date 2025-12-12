package com.example.auditapp;

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

    private static List<LeaveForm> pendingList = new ArrayList<>();
    private static List<AuditRecord> auditRecords = new ArrayList<>();
    private static List<Student> studentList = new ArrayList<>();
    private static Teacher currentTeacher = new Teacher("T999", "陳導師", "jennychou0710@gmail.com");
    private static Student demoStudent = new Student("112403508", "周佳穎", "資管系", "jennychou0710@gmail.com");

    //這邊使用假資料模擬
    static {
        studentList.add(demoStudent);
        studentList.add(new Student("112400001", "王大明", "資管系", "test1@example.com"));
        studentList.add(new Student("112400002", "王小明", "資工系", "test2@example.com"));

        String demoFileUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";
        String demoImgUrl = "https://via.placeholder.com/600x400.png?text=Proof+Document";

        pendingList.add(
                new LeaveForm("L001", "112400001", "事假", "需返鄉", "待審核", "2025-11-25 13:00", "2025-11-25 15:00", null,
                        demoFileUrl));
        pendingList.add(new LeaveForm("L002", "112400002", "病假", "發燒", "待審核", "2025-12-02 09:00", "2025-12-02 12:00",
                demoImgUrl,
                demoFileUrl));
    }

    @GetMapping("/demo")
    public String demoStartPage() {
        return "demo";
    }

    @PostMapping("/demo/trigger")
    public String triggerStudentSubmission(RedirectAttributes redirectAttributes) {
        int nextNum = pendingList.size() + 1;
        String newId = "L" + String.format("%03d", nextNum);
        LeaveForm newLeave = new LeaveForm(
                newId,
                demoStudent.getStudentId(),
                "事假",
                "家中急事需請假",
                "待審核",
                "2025-12-10 08:00",
                "2025-12-10 10:00",
                "https://example.com/proof.pdf",
                "https://example.com/consent.pdf");

        demoStudent.submitLeave(newLeave, pendingList);

        String subject = "【系統通知】您有一筆新的請假審核申請";
        String text = "親愛的 " + currentTeacher.getTeacherName() + " 您好：\n\n" +
                "學生 " + demoStudent.getStudentName() + " (" + demoStudent.getDepartment() + ") 已提交新的請假申請。\n" +
                "請點擊下方連結進入系統進行審核：\nhttp://localhost:8080/";

        sendEmailSafely(currentTeacher.getEmail(), subject, text);

        redirectAttributes.addFlashAttribute("message",
                "模擬成功，學生 " + demoStudent.getStudentName() + "(" + demoStudent.getDepartment() + ") 已提交假單。");
        return "redirect:/demo";
    }

    @PostMapping("/demo/resubmit")
    public String simulateStudentResubmit(RedirectAttributes redirectAttributes) {
        LeaveForm rejectedLeave = pendingList.stream()
                .filter(l -> "不通過".equals(l.getStatus()))
                .findFirst()
                .orElse(null);

        if (rejectedLeave == null) {
            redirectAttributes.addFlashAttribute("message", "目前沒有「不通過」的假單可以重送，請先去駁回一張");
            return "redirect:/demo";
        }

        String newReason = rejectedLeave.getReason() + " (已補件)";
        String newEvidence = "https://example.com/new_proof.pdf";
        String newConsent = "https://example.com/new_consent.pdf";

        demoStudent.modifyLeave(rejectedLeave,
                rejectedLeave.getLeaveType(),
                newReason,
                rejectedLeave.getStartTime(),
                rejectedLeave.getEndTime(),
                newEvidence,
                newConsent);

        String subject = "【系統通知】學生已重新提交假單 (" + rejectedLeave.getId() + ")";
        String text = "導師您好，學生 " + demoStudent.getStudentName() + " 已修正並重送假單，請再次審核。\n" +
                "請點擊下方連結進入系統進行審核：\nhttp://localhost:8080/";

        sendEmailSafely(currentTeacher.getEmail(), subject, text);

        redirectAttributes.addFlashAttribute("message", "模擬成功，學生已修正假單 " + rejectedLeave.getId() + " 並重送，請導師重新審核。");
        return "redirect:/demo";
    }

    @GetMapping("/")
    public String rootPage() {
        return "redirect:/leaves";
    }

    @GetMapping("/leaves")
    public String listLeaves(Model model) {
        model.addAttribute("leaves", pendingList);
        return "list";
    }

    @GetMapping("/leaves/{id}")
    public String leaveDetail(@PathVariable String id, Model model) {
        LeaveForm leave = pendingList.stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null);
        model.addAttribute("leave", leave);
        return "detail";
    }

    @GetMapping("/audit-logs")
    public String viewAuditLogs(Model model) {
        model.addAttribute("logs", auditRecords);
        return "logs";
    }

    @PostMapping("/leaves/{id}/audit")
    public String auditSingle(@PathVariable String id,
            @RequestParam String action,
            @RequestParam(required = false) String reason,
            RedirectAttributes redirectAttributes) {

        LeaveForm leave = pendingList.stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null);

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

            auditRecords.add(0,
                    new AuditRecord(leave.getId(), leave.getStudent().getStudentName(), resultRecord, null, advisorId));
        } else if ("reject".equals(action)) {
            resultStatus = "不通過";
            resultRecord = "駁回";
            leave.setStatus(resultStatus);

            auditRecords.add(0, new AuditRecord(leave.getId(), leave.getStudent().getStudentName(), resultRecord,
                    reason, advisorId));

            String subject = "【假單通知】您的假單未核准";
            String body = "親愛的同學：您的假單 (" + leave.getId() + ") 未通過。原因：" + reason;
            sendEmailSafely(leave.getStudent().getEmail(), subject, body);
        }
    }

    private void sendEmailSafely(String to, String subject, String text) {
        try {
            System.out.println("------------------------------------------------------");
            System.out.println("【系統動作】已發送 Email 至: " + to);
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(text);
                message.setFrom("your-email@gmail.com");
                mailSender.send(message);
                System.out.println(" -> (真實郵件已寄出)");
            } else {
                System.out.println(" -> (模擬模式：未設定 SMTP，僅顯示 Log)");
            }
            System.out.println(" -> [模擬情境] 導師/學生 手機震動，收到標題為 [" + subject + "] 的通知");
            System.out.println("------------------------------------------------------");

        } catch (Exception e) {
            System.err.println("Email Failed: " + e.getMessage());
        }
    }

    public static Student getStudentById(String studentId) {
        for (Student student : studentList) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }
}