
package com.example.demo.controller;

import com.example.demo.entity.Organization;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.DonationLog;

import com.example.demo.repository.DonationLogRepository;

import java.util.Set;



import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Controller
public class VisualizationController {
    @Autowired 
    DonationLogRepository donationLogRepository;

    @GetMapping("/org/view-graph")
    public String viewDonationGraph(HttpSession session) {
        Organization org = (Organization) session.getAttribute("loggedInOrg");
        if (org == null) return "redirect:/login?userType=organization";
        return "donationgraph";
    }

    @GetMapping("/org/get-donation-data")
    @ResponseBody
    public List<List<Object>> getDonationData(HttpSession session) {
        Organization org = (Organization) session.getAttribute("loggedInOrg");
        if (org == null) return new ArrayList<>();  // return empty if not logged in

        // Fetch only donations made to this organization
        List<DonationLog> donations = donationLogRepository.findByOrganization(org);

        Map<String, Map<String, Integer>> groupedData = new LinkedHashMap<>();

        for (DonationLog log : donations) {
            String itemType = log.getItemType();
            String subType = log.getSubType();
            int quantity = log.getQuantity();

            groupedData.putIfAbsent(itemType, new LinkedHashMap<>());
            groupedData.get(itemType).put(subType, groupedData.get(itemType).getOrDefault(subType, 0) + quantity);
        }

        List<List<Object>> donationData = new ArrayList<>();

        List<Object> headerRow = new ArrayList<>();
        headerRow.add("Item Type");

        Set<String> subTypes = new LinkedHashSet<>();
        for (Map<String, Integer> subMap : groupedData.values()) {
            subTypes.addAll(subMap.keySet());
        }

        headerRow.addAll(subTypes);
        donationData.add(headerRow);

        for (Map.Entry<String, Map<String, Integer>> entry : groupedData.entrySet()) {
            List<Object> row = new ArrayList<>();
            row.add(entry.getKey());

            for (String subtype : subTypes) {
                row.add(entry.getValue().getOrDefault(subtype, 0));
            }

            donationData.add(row);
        }

        return donationData;
    }

}
