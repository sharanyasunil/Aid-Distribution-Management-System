package com.example.demo.service;

import com.example.demo.entity.Donor;
import com.example.demo.entity.Organization;
import com.example.demo.repository.DonorRepository;
import com.example.demo.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    public boolean registerDonor(Donor donor) {
        if (donor.getName() == null || donor.getPassword() == null ||
            donor.getEmail() == null || donor.getMobileNumber() == null) {
            return false;
        }
        donorRepository.save(donor);
        return true;
    }

    public boolean registerOrg(Organization org) {
        if (org.getName() == null || org.getPassword() == null || org.getEmail() == null ||
            org.getAddress() == null || org.getContactPersonName() == null ||
            org.getMobileNumber() == null || org.getTypeOfOrg() == null) {
            return false;
        }
        organizationRepository.save(org);
        return true;
    }

    public String login(String name, String password) {
        return donorRepository.findByName(name)
                .filter(d -> d.getPassword().equals(password))
                .map(d -> "donor")
                .orElseGet(() ->
                    organizationRepository.findByName(name)
                        .filter(o -> o.getPassword().equals(password))
                        .map(o -> "organization")
                        .orElse("invalid"));
    }

    public boolean validateDonor(String name, String password) {
        return donorRepository.findByName(name)
                .map(d -> d.getPassword().equals(password))
                .orElse(false);
    }

    public boolean validateOrganization(String name, String password) {
        return organizationRepository.findByName(name)
                .map(o -> o.getPassword().equals(password))
                .orElse(false);
    }

    // New method to fetch organization object by name
    public Organization getOrgByName(String name) {
        return organizationRepository.findByName(name).orElse(null);
    }

    public Donor getDonorByName(String name) {
        return donorRepository.findByName(name).orElse(null);
    }
    public Organization getOrgById(Long id) {
        return organizationRepository.findById(id).orElse(null);
    }
    
}
