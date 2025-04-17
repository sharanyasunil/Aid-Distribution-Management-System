package com.example.demo.cor;

import com.example.demo.entity.Donor;
import com.example.demo.entity.DonorDonationItem;
import com.example.demo.entity.Organization;
import com.example.demo.entity.OrganizationRequirement;
import com.example.demo.repository.DonorDonationItemRepository;
import com.example.demo.repository.OrganizationRequirementRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.List;

@Component
public class DonationExistsHandler implements DonationValidationHandler {

    private DonationValidationHandler next;

    @Autowired
    private DonorDonationItemRepository donorDonationItemRepository;

    @Autowired
    private OrganizationRequirementRepository requirementRepository;

    

    @Override
    public void setNext(DonationValidationHandler next) {
        this.next = next;
    }

    @Override
    public String handle(String itemType, String subType, int quantity, Long orgId, HttpSession session, Model model) {
        Donor donor = (Donor) session.getAttribute("loggedInDonor");
        Organization org = (Organization) session.getAttribute("orgCache");

        List<DonorDonationItem> matchingDonorItems =
                donorDonationItemRepository.findAllByDonorAndItemTypeAndSubType(donor, itemType, subType);

        if (matchingDonorItems == null || matchingDonorItems.isEmpty()) {
            model.addAttribute("error", "You have not listed this item for donation.");
            return "fail";
        }

        List<OrganizationRequirement> requirements =
            requirementRepository.findByOrganizationAndItemTypeAndSubType(org, itemType, subType);

        OrganizationRequirement requirement = requirements.isEmpty() ? null : requirements.get(0);


        if (requirement == null) {
            model.addAttribute("error", "Organization has not requested this item.");
            return "fail";
        }

        session.setAttribute("donorItemList", matchingDonorItems);
        session.setAttribute("orgReq", requirement);

        return next != null ? next.handle(itemType, subType, quantity, orgId, session, model) : null;
    }
}
