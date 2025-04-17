package com.example.demo.cor;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component
public class SubTypeRestrictionHandler implements DonationValidationHandler {

    private DonationValidationHandler next;

    @Override
    public void setNext(DonationValidationHandler next) {
        this.next = next;
    }

    @Override
    public String handle(String itemType, String subType, int quantity, Long orgId, HttpSession session, Model model) {
        

        boolean valid = switch (itemType.toLowerCase()) {
            case "food" -> subType.equalsIgnoreCase("veg") || subType.equalsIgnoreCase("non-veg");
            case "clothes" -> subType.equalsIgnoreCase("t-shirt") || subType.equalsIgnoreCase("pant")
                              || subType.equalsIgnoreCase("shorts") || subType.equalsIgnoreCase("jacket");
            case "books" -> subType.equalsIgnoreCase("textbook") || subType.equalsIgnoreCase("notebook");
            default -> false;
        };

        if (!valid) {
            model.addAttribute("error", "Invalid subtype '" + subType + "' for item type '" + itemType + "'.");
            return "fail";
        }

        return next != null ? next.handle(itemType, subType, quantity, orgId, session, model) : null;
    }
}
