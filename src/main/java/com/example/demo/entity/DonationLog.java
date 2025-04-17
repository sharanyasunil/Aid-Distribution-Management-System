package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DonationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Donor donor;

    @ManyToOne
    private Organization organization;

    private String itemType;
    private String subType;
    private int quantity;

    private LocalDateTime donatedAt;
}