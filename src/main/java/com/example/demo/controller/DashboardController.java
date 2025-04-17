
package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {
    @GetMapping("/dashboard/donor")
    public String donorDashboard() { return "donor_dashboard"; }

    @GetMapping("/dashboard/org")
    public String orgDashboard() { return "org_dashboard"; }
}
