package com.gstbilling.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gstbilling.models.BusinessDetails;

@Repository
public interface BusinessDetailsRepository extends JpaRepository<BusinessDetails, Long> {
    
    // Find business by GSTIN
    Optional<BusinessDetails> findByGstin(String gstin);
    
    // Check if business exists by GSTIN
    boolean existsByGstin(String gstin);
    
    // Find business by email
    Optional<BusinessDetails> findByEmail(String email);
    
    // Find by the name
    Optional<BusinessDetails> findByBusinessName(String name);
}