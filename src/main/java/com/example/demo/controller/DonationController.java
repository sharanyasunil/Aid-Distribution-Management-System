package com.example.demo.controller;


import com.example.demo.cor.DonationExistsHandler;
import com.example.demo.cor.QuantityValidationHandler;
import com.example.demo.cor.SubTypeRestrictionHandler;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DonationController {


    @Autowired
    private DonorDonationItemRepository donorDonationItemRepository;

    @Autowired
    private OrganizationRequirementRepository requirementRepository;

    @Autowired
    private DonationLogRepository donationLogRepository;

    @Autowired
    private PointsTransactionLogRepository pointsTransactionLogRepository;

    @Autowired
    private AuthService authService;
    @Autowired
    private DonationExistsHandler donationExistsHandler;
    @Autowired
    private QuantityValidationHandler quantityValidationHandler;

    @Autowired
    private SubTypeRestrictionHandler subTypeRestrictionHandler;


    

    // View matched orgs
    @GetMapping("/donor/matched-orgs")
    public String viewMatchedOrganizations(HttpSession session, Model model) {
        Donor donor = (Donor) session.getAttribute("loggedInDonor");
        if (donor == null) return "redirect:/login?userType=donor";

        List<DonorDonationItem> donorItems = donorDonationItemRepository.findByDonor(donor);
        List<OrganizationRequirement> allRequirements = requirementRepository.findAll();

        Map<Organization, List<OrganizationRequirement>> matched = new HashMap<>();
        for (OrganizationRequirement req : allRequirements) {
            for (DonorDonationItem item : donorItems) {
                if (req.getItemType().equalsIgnoreCase(item.getItemType()) &&
                    req.getSubType().equalsIgnoreCase(item.getSubType()) &&
                    item.getQuantity() > 0 && req.getQuantity() > 0) {
                    matched.computeIfAbsent(req.getOrganization(), k -> new ArrayList<>()).add(req);
                    break;
                }
            }
        }
        model.addAttribute("matchedOrgs", matched);
        return "matched_organizations";
    }

    // Confirm donation (GET)
    @GetMapping("/donor/confirm-donation")
    public String confirmDonation(@RequestParam Long orgId, HttpSession session, Model model) {
        Donor donor = (Donor) session.getAttribute("loggedInDonor");
        if (donor == null) return "redirect:/login?userType=donor";

        Organization org = authService.getOrgById(orgId);
        List<DonorDonationItem> donorItems = donorDonationItemRepository.findByDonor(donor);
        List<OrganizationRequirement> requirements = requirementRepository.findByOrganization(org);

        Set<String> itemTypes = donorItems.stream()
            .map(DonorDonationItem::getItemType).collect(Collectors.toSet());

        Map<String, List<String>> typeToSubTypes = donorItems.stream()
            .collect(Collectors.groupingBy(
                DonorDonationItem::getItemType,
                Collectors.mapping(DonorDonationItem::getSubType, 
                    Collectors.collectingAndThen(Collectors.toSet(), ArrayList::new))
            ));
        

        model.addAttribute("itemTypes", itemTypes);
        model.addAttribute("typeToSubTypes", typeToSubTypes);
        model.addAttribute("requirements", requirements);
        model.addAttribute("org", org);
        return "donation_confirmation";
    }

    // Process donation (POST)
    @PostMapping("/donor/process-donation")
    public String processDonation(@RequestParam Long orgId,
                                @RequestParam String itemType,
                                @RequestParam String subType,
                                @RequestParam int quantity,
                                HttpSession session,
                                Model model) {

        Donor donor = (Donor) session.getAttribute("loggedInDonor");
        if (donor == null) return "redirect:/login?userType=donor";

        Organization org = authService.getOrgById(orgId);
        session.setAttribute("orgCache", org);

        //COR is implemented here

        donationExistsHandler.setNext(subTypeRestrictionHandler);
        subTypeRestrictionHandler.setNext(quantityValidationHandler);
        String result = donationExistsHandler.handle(itemType, subType, quantity, orgId, session, model);
        if ("fail".equals(result)) return confirmDonation(orgId, session, model);

        @SuppressWarnings("unchecked")
        List<DonorDonationItem> donorItems = (List<DonorDonationItem>) session.getAttribute("donorItemList");
        OrganizationRequirement requirement = (OrganizationRequirement) session.getAttribute("orgReq");

        // Deduct quantity from donor items sequentially
        int remainingToDeduct = quantity;
        for (DonorDonationItem item : donorItems) {
            if (remainingToDeduct <= 0) break;
            int available = item.getQuantity();
            if (available <= 0) continue;

            int deduct = Math.min(available, remainingToDeduct);
            item.setQuantity(available - deduct);
            remainingToDeduct -= deduct;

            donorDonationItemRepository.save(item); // Save after each deduction
        }

        // Deduct from org requirement
        requirement.setQuantity(requirement.getQuantity() - quantity);
        requirementRepository.save(requirement);

        // Log the donation
        DonationLog log = new DonationLog();
        log.setDonor(donor);
        log.setOrganization(org);
        log.setItemType(itemType);
        log.setSubType(subType);
        log.setQuantity(quantity);
        log.setDonatedAt(java.time.LocalDateTime.now());
        donationLogRepository.save(log);

        PointsTransactionLog pointsLog = new PointsTransactionLog();
        pointsLog.setDonor(donor);
        pointsLog.setTransactionType("DONATION");
        pointsLog.setItemType(itemType);
        pointsLog.setSubType(subType);
        pointsLog.setQuantity(quantity);
        pointsLog.setPointsAwarded(quantity * 10);
        pointsLog.setTransactionTime(java.time.LocalDateTime.now());
        pointsTransactionLogRepository.save(pointsLog);

        model.addAttribute("message", "✅ Donation successful! Thank you for your contribution.");
        return "donation_success";
    }



    // View donation logs (org side)
    @GetMapping("/org/view-donations")
    public String viewOrgDonations(HttpSession session, Model model) {
        Organization org = (Organization) session.getAttribute("loggedInOrg");
        if (org == null) return "redirect:/login?userType=organization";

        List<DonationLog> logs = donationLogRepository.findByOrganization(org);
        model.addAttribute("logs", logs);
        model.addAttribute("orgName", org.getName());
        return "org_view_donations";
    }
} 
