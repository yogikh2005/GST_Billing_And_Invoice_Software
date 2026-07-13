package com.gstbilling.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gstbilling.dao.BusinessDetailsRepository;
import com.gstbilling.dto.BusinessDetailsDto;
import com.gstbilling.models.BusinessDetails;

@Service
@Transactional
public class BusinessDetailsService {

    @Autowired
    private BusinessDetailsRepository businessDetailsRepository;

    public BusinessDetails createBusinessDetails(BusinessDetailsDto dto) {
        // Validate GSTIN uniqueness
        if (businessDetailsRepository.existsByGstin(dto.getGstin())) {
            throw new RuntimeException("Business with GSTIN " + dto.getGstin() + " already exists");
        }

        BusinessDetails business = BusinessDetails.builder()
                .businessName(dto.getBusinessName())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .gstin(dto.getGstin())
                .email(dto.getEmail())
                .mobileNo(dto.getMobileNo())
                .phoneNo(dto.getPhoneNo())
                .website(dto.getWebsite())
                .logoPath(dto.getLogoPath())
                .bankName(dto.getBankName())
                .accountNumber(dto.getAccountNumber())
                .ifscCode(dto.getIfscCode())
                .branch(dto.getBranch())
                .termsAndConditions(dto.getTermsAndConditions())
                .build();

        return businessDetailsRepository.save(business);
    }

   
    public List<BusinessDetails> getAllBusinessDetails() {
        return businessDetailsRepository.findAll();
    }

    public BusinessDetails getBusinessDetailsById(Long id) {
        return businessDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found with id: " + id));
    }
    
    public BusinessDetails getBusinessDetailsByName(String name)
    {
    		return businessDetailsRepository.findByBusinessName(name)
    				.orElseThrow(()-> new RuntimeException("Business not found with name: " + name));
    }

    public BusinessDetails getBusinessByGstin(String gstin) {
        return businessDetailsRepository.findByGstin(gstin)
                .orElseThrow(() -> new RuntimeException("Business not found with GSTIN: " + gstin));
    }

    public BusinessDetails getDefaultBusinessDetails() {
        List<BusinessDetails> businesses = businessDetailsRepository.findAll();
        if (businesses.isEmpty()) {
            throw new RuntimeException("No business details found. Please create business details first.");
        }
        return businesses.get(0);
    }

 
    public BusinessDetails updateBusinessDetails(Long id, BusinessDetailsDto dto) {
        BusinessDetails existing = getBusinessDetailsById(id);

        // Check GSTIN uniqueness if changed
        if (!existing.getGstin().equals(dto.getGstin())) {
            if (businessDetailsRepository.existsByGstin(dto.getGstin())) {
                throw new RuntimeException("Business with GSTIN " + dto.getGstin() + " already exists");
            }
        }

        // Update all fields
        existing.setBusinessName(dto.getBusinessName());
        existing.setAddress(dto.getAddress());
        existing.setCity(dto.getCity());
        existing.setState(dto.getState());
        existing.setPincode(dto.getPincode());
        existing.setGstin(dto.getGstin());
        existing.setEmail(dto.getEmail());
        existing.setMobileNo(dto.getMobileNo());
        existing.setPhoneNo(dto.getPhoneNo());
        existing.setWebsite(dto.getWebsite());
        existing.setLogoPath(dto.getLogoPath());
        existing.setBankName(dto.getBankName());
        existing.setAccountNumber(dto.getAccountNumber());
        existing.setIfscCode(dto.getIfscCode());
        existing.setBranch(dto.getBranch());
        existing.setTermsAndConditions(dto.getTermsAndConditions());

        return businessDetailsRepository.save(existing);
    }

    
    public void deleteBusinessDetails(Long id) {
        BusinessDetails existing = getBusinessDetailsById(id);
        businessDetailsRepository.delete(existing);
    }

    
    public boolean existsById(Long id) {
        return businessDetailsRepository.existsById(id);
    }

    public boolean existsByGstin(String gstin) {
        return businessDetailsRepository.existsByGstin(gstin);
    }
}