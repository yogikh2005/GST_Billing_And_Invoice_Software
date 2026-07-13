package com.gstbilling.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "business_details")
public class BusinessDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String businessName;

    @Column(length = 500)
    private String address;
    
    private String city;
    
    private String state;
    
    private String pincode;

    @Column(unique = true, nullable = false)
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

    @Column(length = 1000)
    private String termsAndConditions;

   

    
}