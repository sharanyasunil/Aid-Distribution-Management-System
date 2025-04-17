package com.example.demo.controller;

import com.example.demo.entity.Organization;
import com.example.demo.entity.OrganizationRequirement;
import com.example.demo.repository.OrganizationRequirementRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class OrganizationController {

    @Autowired
    private OrganizationRequirementRepository requirementRepository;

    // ✅ Show requirement form
    @GetMapping("/org/requirements")
    public String showRequirementForm() {
        return "org_requirements_form";
    }

    // ✅ Submit new requirement
    @PostMapping("/org/submit-requirements")
    public String submitRequirement(@RequestParam String itemType,
                                    @RequestParam String subType,
                                    @RequestParam int quantity,
                                    HttpSession session,
                                    Model model) {
        Organization loggedInOrg = (Organization) session.getAttribute("loggedInOrg");
        if (loggedInOrg == null) {
            return "redirect:/login?userType=organization";
        }

        // ✅ Check if same itemType + subType already exists
        List<OrganizationRequirement> existingList = requirementRepository
                .findByOrganizationAndItemTypeAndSubType(loggedInOrg, itemType, subType);

        if (!existingList.isEmpty()) {
            // ✅ Update quantity on the first matching row
            OrganizationRequirement existing = existingList.get(0);
            existing.setQuantity(existing.getQuantity() + quantity);
            requirementRepository.save(existing);
        } else {
            // ✅ Insert new row if no match exists
            OrganizationRequirement req = new OrganizationRequirement();
            req.setItemTypeAndSubType(itemType, subType);
            req.setQuantity(quantity);
            req.setOrganization(loggedInOrg);
            requirementRepository.save(req);
        }

        model.addAttribute("requirements", requirementRepository.findByOrganization(loggedInOrg));
        return "org_requirements_list";
    }


    // ✅ View all requirements for logged-in org
    @GetMapping("/view-requirements")
    public String viewRequirements(HttpSession session, Model model) {
        Organization loggedInOrg = (Organization) session.getAttribute("loggedInOrg");

        if (loggedInOrg == null) {
            return "redirect:/login?userType=organization";
        }

        List<OrganizationRequirement> requirements = requirementRepository.findByOrganization(loggedInOrg);

        model.addAttribute("requirements", requirements);
        model.addAttribute("orgName", loggedInOrg.getName());

        return "view_requirements";
    }
}
