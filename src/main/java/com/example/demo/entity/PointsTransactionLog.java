package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointsTransactionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Donor donor;

    private String transactionType; // "DONATION", "DONATE_POINTS", "CASHBACK"

    private String itemType;
    private String subType;
    private int quantity;

    private int pointsAwarded;

    private LocalDateTime transactionTime;
}
