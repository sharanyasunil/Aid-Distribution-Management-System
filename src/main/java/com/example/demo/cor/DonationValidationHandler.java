package com.example.demo.cor;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

public interface DonationValidationHandler {
    void setNext(DonationValidationHandler next);
    String handle(String itemType, String subType, int quantity, Long orgId, HttpSession session, Model model);
}