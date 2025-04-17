package com.example.demo.controller;

import com.example.demo.entity.Donor;
import com.example.demo.entity.DonorDonationItem;
import com.example.demo.repository.DonorDonationItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DonorController {

    @Autowired
    private DonorDonationItemRepository donorDonationItemRepository;

    // ✅ Show donor's donation item form
    @GetMapping("/donor/donation-items")
    public String showDonationForm() {
        return "donor_donation_form";
    }

    // ✅ Handle submission of donor items
    @PostMapping("/donor/submit-donation")
    public String submitDonation(@RequestParam String itemType,
                                @RequestParam String subType,
                                @RequestParam int quantity,
                                HttpSession session,
                                Model model) {
        Donor loggedInDonor = (Donor) session.getAttribute("loggedInDonor");
        if (loggedInDonor == null) {
            return "redirect:/login?userType=donor";
        }

        // Check if the itemType + subType combo already exists for the donor
        List<DonorDonationItem> existingItems = donorDonationItemRepository
                .findAllByDonorAndItemTypeAndSubType(loggedInDonor, itemType, subType);

        if (!existingItems.isEmpty()) {
            // Add quantity to the first matching record
            DonorDonationItem existing = existingItems.get(0);
            existing.setQuantity(existing.getQuantity() + quantity);
            donorDonationItemRepository.save(existing);

            // Optionally delete other duplicates if you want to keep only one entry
            for (int i = 1; i < existingItems.size(); i++) {
                donorDonationItemRepository.delete(existingItems.get(i));
            }
        } else {
            // No existing item found, so create a new one
            DonorDonationItem item = new DonorDonationItem();
            item.setItemTypeAndSubType(itemType, subType);
            item.setQuantity(quantity);
            item.setDonor(loggedInDonor);
            donorDonationItemRepository.save(item);
        }

        model.addAttribute("donations", donorDonationItemRepository.findByDonor(loggedInDonor));
        return "donor_donation_list";
    }


    // ✅ View donor's submitted donations
    @GetMapping("/donor/view-donations")
    public String viewDonations(HttpSession session, Model model) {
        Donor loggedInDonor = (Donor) session.getAttribute("loggedInDonor");
        if (loggedInDonor == null) {
            return "redirect:/login?userType=donor";
        }

        List<DonorDonationItem> donations = donorDonationItemRepository.findByDonor(loggedInDonor);
        model.addAttribute("donations", donations);
        model.addAttribute("donorName", loggedInDonor.getName());

        return "view_donation_list";
    }
}
