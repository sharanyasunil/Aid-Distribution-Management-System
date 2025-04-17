
package com.example.demo.controller;

import com.example.demo.entity.Donor;
import com.example.demo.entity.Organization;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    @Autowired private AuthService authService;

    @GetMapping("/")
    public String home() { return "select"; }

    @GetMapping("/signup/donor")
    public String donorSignupForm(@RequestParam(required = false) String userType, Model model) {
        model.addAttribute("userType", userType);
        return "donor_signup";
    }

    @PostMapping("/signup/donor")
    public String registerDonor(@ModelAttribute Donor donor, @RequestParam(required = false) String userType, Model model) {
        if (authService.registerDonor(donor)) return "redirect:/login?userType=" + userType;
        model.addAttribute("error", "All fields are required.");
        model.addAttribute("userType", userType);
        return "donor_signup";
    }

    @GetMapping("/signup/org")
    public String orgSignupForm(@RequestParam(required = false) String userType, Model model) {
        model.addAttribute("userType", userType);
        return "org_signup";
    }

    @PostMapping("/signup/org")
    public String registerOrg(@ModelAttribute Organization org, @RequestParam(required = false) String userType, Model model) {
        if (authService.registerOrg(org)) return "redirect:/login?userType=" + userType;
        model.addAttribute("error", "All fields are required.");
        model.addAttribute("userType", userType);
        return "org_signup";
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String userType, Model model) {
        model.addAttribute("userType", userType);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String name, @RequestParam String password, @RequestParam String userType, Model model, HttpSession session) {
        boolean isValid = false;
        if ("donor".equalsIgnoreCase(userType)) {
            isValid = authService.validateDonor(name, password);
            if (isValid) {
                session.setAttribute("loggedInDonor", authService.getDonorByName(name));
                return "redirect:/dashboard/donor";
            }
        } else if ("organization".equalsIgnoreCase(userType)) {
            isValid = authService.validateOrganization(name, password);
            if (isValid) {
                session.setAttribute("loggedInOrg", authService.getOrgByName(name));
                return "redirect:/dashboard/org";
            }
        }
        model.addAttribute("error", "Invalid credentials");
        model.addAttribute("userType", userType);
        return "login";
    }
}