package com.gstbilling.controler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gstbilling.dto.BusinessDetailsDto;
import com.gstbilling.models.BusinessDetails;
import com.gstbilling.service.BusinessDetailsService;

@RestController
@RequestMapping("/owner/business")
@PreAuthorize("hasRole('OWNER')")
public class BusinessDetailsController {

    @Autowired
    private BusinessDetailsService businessDetailsService;

    
    @GetMapping
    public ResponseEntity<List<BusinessDetails>> getAllBusinesses() {
        List<BusinessDetails> businesses = businessDetailsService.getAllBusinessDetails();
        return ResponseEntity.ok(businesses);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<BusinessDetails> getBusinessById(@PathVariable Long id) {
        try {
            BusinessDetails business = businessDetailsService.getBusinessDetailsById(id);
            return ResponseEntity.ok(business);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    
    @GetMapping("/default")
    public ResponseEntity<BusinessDetails> getDefaultBusiness() {
        try {
            BusinessDetails business = businessDetailsService.getDefaultBusinessDetails();
            return ResponseEntity.ok(business);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<BusinessDetails> createBusiness(@RequestBody BusinessDetailsDto dto) {
        try {
            BusinessDetails created = businessDetailsService.createBusinessDetails(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
 
    @PutMapping("/{id}")
    public ResponseEntity<BusinessDetails> updateBusiness(
            @PathVariable Long id,
            @RequestBody BusinessDetailsDto dto) {
        try {
            BusinessDetails updated = businessDetailsService.updateBusinessDetails(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBusiness(@PathVariable Long id) {
        try {
            businessDetailsService.deleteBusinessDetails(id);
            return ResponseEntity.ok("Business deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Business not found with id: " + id);
        }
    }
}