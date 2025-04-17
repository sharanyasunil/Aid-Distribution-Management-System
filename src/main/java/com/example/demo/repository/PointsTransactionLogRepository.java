package com.example.demo.repository;

import com.example.demo.entity.Donor;
import com.example.demo.entity.PointsTransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointsTransactionLogRepository extends JpaRepository<PointsTransactionLog, Long> {
    List<PointsTransactionLog> findByDonor(Donor donor);
    List<PointsTransactionLog> findByDonorOrderByTransactionTimeDesc(Donor donor);

}
