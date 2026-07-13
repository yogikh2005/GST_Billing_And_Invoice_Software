package com.gstbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDetailsDto {
    
    private String businessName;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String gstin;
    private String email;
    private String mobileNo;
    private String phoneNo;
    private String website;
    private String logoPath;
    
    // Bank details
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String branch;
    
    private String termsAndConditions;
}