package com.example.demo.cor;

import com.example.demo.entity.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class QuantityValidationHandler implements DonationValidationHandler {

    private DonationValidationHandler next;

    @Override
    public void setNext(DonationValidationHandler next) {
        this.next = next;
    }

    @Override
    public String handle(String itemType, String subType, int quantity, Long orgId, HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<DonorDonationItem> donorItems = (List<DonorDonationItem>) session.getAttribute("donorItemList");
        OrganizationRequirement req = (OrganizationRequirement) session.getAttribute("orgReq");

        if (donorItems == null || donorItems.isEmpty() || req == null) {
            model.addAttribute("error", "Internal error: donation data missing.");
            return "fail";
        }

        int totalAvailable = donorItems.stream().mapToInt(DonorDonationItem::getQuantity).sum();

        if (quantity > totalAvailable) {
            model.addAttribute("error", "You cannot donate more than you have. Total available: " + totalAvailable);
            return "fail";
        }

        if (quantity > req.getQuantity()) {
            model.addAttribute("error", "The organization only needs " + req.getQuantity() + " of this item.");
            return "fail";
        }

        return next != null ? next.handle(itemType, subType, quantity, orgId, session, model) : null;
    }

}
