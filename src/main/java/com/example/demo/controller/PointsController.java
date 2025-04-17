package com.example.demo.controller;

import com.example.demo.entity.Donor;
import com.example.demo.entity.PointsTransactionLog;
import com.example.demo.repository.PointsTransactionLogRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class PointsController {

    @Autowired
    private PointsTransactionLogRepository pointsTransactionLogRepository;

    // ✅ Show Points Page
    @GetMapping("/donor/points")
    public String viewPoints(HttpSession session, Model model) {
        Donor donor = (Donor) session.getAttribute("loggedInDonor");
        if (donor == null) return "redirect:/login?userType=donor";

        List<PointsTransactionLog> transactions = pointsTransactionLogRepository
                .findByDonorOrderByTransactionTimeDesc(donor);
        int totalPoints = transactions.stream().mapToInt(PointsTransactionLog::getPointsAwarded).sum();

        model.addAttribute("transactions", transactions);
        model.addAttribute("totalPoints", totalPoints);
        model.addAttribute("donorName", donor.getName());
        return "donor_view_points";
    }

    // ✅ Donate Points - GET
    @GetMapping("/donor/points/donate")
    public String donatePointsPage(HttpSession session, Model model,
                                   @RequestParam(value = "success", required = false) String success,
                                   @RequestParam(value = "error", required = false) String error) {

        Donor donor = (Donor) session.getAttribute("loggedInDonor");
        if (donor == null) return "redirect:/login?userType=donor";

        int totalPoints = pointsTransactionLogRepository.findByDonor(donor)
                .stream().mapToInt(PointsTransactionLog::getPointsAwarded).sum();

        model.addAttribute("totalPoints", totalPoints);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        return "donate_points";
    }

    // ✅ Donate Points - POST
    @PostMapping("/donor/points/donate")
    public String donatePoints(@RequestParam int points, HttpSession session, Model model) {
        Donor donor = (Donor) session.getAttribute("loggedInDonor");
        if (donor == null) return "redirect:/login?userType=donor";

        int totalPoints = pointsTransactionLogRepository.findByDonor(donor)
                .stream().mapToInt(PointsTransactionLog::getPointsAwarded).sum();

        if (points > totalPoints) {
            return "redirect:/donor/points/donate?error=You+cannot+donate+more+points+than+you+have!";
        }

        // Deduct full points (not shown in this log)
        PointsTransactionLog deductionLog = new PointsTransactionLog();
        deductionLog.setDonor(donor);
        deductionLog.setTransactionType("POINTS_USED");
        deductionLog.setItemType("POINTS");
        deductionLog.setSubType("Internal Deduction");
        deductionLog.setQuantity(points);
        deductionLog.setPointsAwarded(-points);
        deductionLog.setTransactionTime(LocalDateTime.now());
        pointsTransactionLogRepository.save(deductionLog);

        // Add reward points log
        PointsTransactionLog rewardLog = new PointsTransactionLog();
        rewardLog.setDonor(donor);
        rewardLog.setTransactionType("DONATE_POINTS");
        rewardLog.setItemType("POINTS");
        rewardLog.setSubType("points");
        rewardLog.setQuantity(points);
        rewardLog.setPointsAwarded(points / 2); // Only show reward
        rewardLog.setTransactionTime(LocalDateTime.now());
        pointsTransactionLogRepository.save(rewardLog);

        return "redirect:/donor/points/donate?success=" +
                points + "+points+donated+successfully%21+Reward%3A+" + (points / 2) + "+points.";
    }

    // ✅ Cashback - GET
    @GetMapping("/donor/points/cashback")
    public String cashbackPage(HttpSession session, Model model,
                               @RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "success", required = false) String success) {

        Donor donor = (Donor) session.getAttribute("loggedInDonor");
        if (donor == null) return "redirect:/login?userType=donor";

        int totalPoints = pointsTransactionLogRepository.findByDonor(donor)
                .stream().mapToInt(PointsTransactionLog::getPointsAwarded).sum();

        model.addAttribute("totalPoints", totalPoints);
        model.addAttribute("error", error);
        model.addAttribute("success", success);
        return "cashback_points";
    }

    // ✅ Cashback - POST
    @PostMapping("/donor/points/cashback")
    public String processCashback(@RequestParam int points,
                                @RequestParam(name = "method") String paymentMethod,
                                HttpSession session,
                                Model model) {
        Donor donor = (Donor) session.getAttribute("loggedInDonor");
        if (donor == null) return "redirect:/login?userType=donor";

        int totalPoints = pointsTransactionLogRepository.findByDonor(donor)
                .stream().mapToInt(PointsTransactionLog::getPointsAwarded).sum();

        if (points > totalPoints) {
            return "redirect:/donor/points/cashback?error=You+cannot+redeem+more+points+than+you+have!";
        }

        PointsTransactionLog log = new PointsTransactionLog();
        log.setDonor(donor);
        log.setTransactionType("CASHBACK");
        log.setItemType("points");
        log.setSubType(paymentMethod);
        log.setQuantity(points);
        log.setPointsAwarded(-points);
        log.setTransactionTime(LocalDateTime.now());
        pointsTransactionLogRepository.save(log);

        double cashbackValue = points * 0.10;
        String successMessage = "Cashback of ₹" + cashbackValue + " sent via " + paymentMethod;
        return "redirect:/donor/points/cashback?success=" +
                java.net.URLEncoder.encode(successMessage, java.nio.charset.StandardCharsets.UTF_8);
    }

}
