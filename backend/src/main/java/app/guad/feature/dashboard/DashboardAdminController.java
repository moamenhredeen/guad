package app.guad.feature.dashboard;

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
