package app.guad.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
@RequestMapping("/admin")
public class DashboardAdminController {

    @GetMapping("dashboard")
    public String dashboard(){
        return "admin/dashboard";
    }
}
