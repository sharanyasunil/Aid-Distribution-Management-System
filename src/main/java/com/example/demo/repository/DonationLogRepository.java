package com.example.demo.repository;

import com.example.demo.entity.DonationLog;
import com.example.demo.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface DonationLogRepository extends JpaRepository<DonationLog, Long> {
    List<DonationLog> findByOrganization(Organization org);

    
}