package com.example.demo.repository;

import com.example.demo.entity.Donor;
import com.example.demo.entity.DonorDonationItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonorDonationItemRepository extends JpaRepository<DonorDonationItem, Long> {
    List<DonorDonationItem> findByDonor(Donor donor);
    List<DonorDonationItem> findByDonorAndItemTypeAndSubType(Donor donor, String itemType, String subType);
    List<DonorDonationItem> findAllByDonorAndItemTypeAndSubType(Donor donor, String itemType, String subType);


}