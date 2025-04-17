package com.example.demo.repository;

import com.example.demo.entity.Organization;
import com.example.demo.entity.OrganizationRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface OrganizationRequirementRepository extends JpaRepository<OrganizationRequirement, Long> {
    List<OrganizationRequirement> findByOrganization(Organization organization);
    List<OrganizationRequirement> findByOrganizationAndItemTypeAndSubType(Organization org, String itemType, String subType);



}
