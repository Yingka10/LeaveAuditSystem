package com.example.leaveapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LeaveController {

    // 模擬資料庫 (Mock Data)
    private static List<LeaveRequest> leaveDb = new ArrayList<>();

    // 初始化假資料
    static {
        leaveDb.add(new LeaveRequest("L001", "周佳穎", "II2403508", "事假", "2025-12-01", "家裡有事需返鄉", "待審核"));
        leaveDb.add(new LeaveRequest("L002", "王小明", "II2400001", "病假", "2025-12-02", "感冒發燒", "待審核"));
        leaveDb.add(new LeaveRequest("L003", "陳大文", "II2400002", "公假", "2025-12-03", "參加程式競賽", "待審核"));
        leaveDb.add(new LeaveRequest("L004", "林雅婷", "II2400003", "事假", "2025-12-05", "私人行程", "待審核"));
    }

    // 1. 登入頁面
    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    // 2. 處理登入 (簡單模擬：帳號 teacher, 密碼 1234)
    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, Model model) {
        if ("teacher".equals(username) && "1234".equals(password)) {
            return "redirect:/leaves";
        } else {
            model.addAttribute("error", "帳號或密碼錯誤");
            return "login";
        }
    }

    // 3. 顯示假單列表 (Dashboard)
    @GetMapping("/leaves")
    public String listLeaves(Model model) {
        model.addAttribute("leaves", leaveDb);
        return "list";
    }

    // 4. 查看假單詳細內容
    @GetMapping("/leaves/{id}")
    public String leaveDetail(@PathVariable String id, Model model) {
        LeaveRequest leave = leaveDb.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .orElse(null);
        model.addAttribute("leave", leave);
        return "detail";
    }

    // 5. 單筆審核 (在詳細頁面操作)
    @PostMapping("/leaves/{id}/audit")
    public String auditSingle(@PathVariable String id, @RequestParam String action) {
        LeaveRequest leave = leaveDb.stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null);
        if (leave != null) {
            if ("approve".equals(action)) leave.setStatus("通過");
            else if ("reject".equals(action)) leave.setStatus("不通過");
        }
        return "redirect:/leaves";
    }

    // 6. 批次審核 (在列表頁面操作)
    @PostMapping("/leaves/batch")
    public String auditBatch(@RequestParam(required = false) List<String> ids, @RequestParam String action) {
        if (ids != null && !ids.isEmpty()) {
            for (String id : ids) {
                LeaveRequest leave = leaveDb.stream().filter(l -> l.getId().equals(id)).findFirst().orElse(null);
                if (leave != null) {
                    if ("approve".equals(action)) leave.setStatus("通過");
                    else if ("reject".equals(action)) leave.setStatus("不通過");
                }
            }
        }
        return "redirect:/leaves";
    }
    
    // 7. 登出功能
    @GetMapping("/logout")
    public String logout() {
        // 這裡可以加入清除 Session 的邏輯，但簡單起見，直接導回首頁(登入頁)
        return "redirect:/";
    }
}
