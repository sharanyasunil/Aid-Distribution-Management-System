package com.example.demo.entity;

import com.example.demo.flyweight.DonationItemType;
import com.example.demo.flyweight.DonationItemTypeFactory;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonorDonationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    private String itemType;
    private String subType;

    @Transient
    private DonationItemType donationItemType;

    public void setItemTypeAndSubType(String itemType, String subType) {
        this.itemType = itemType;
        this.subType = subType;
        this.donationItemType = DonationItemTypeFactory.getItemType(itemType, subType);

    }

    public String getItemType() {
        return itemType;
    }

    public String getSubType() {
        return subType;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }
}
