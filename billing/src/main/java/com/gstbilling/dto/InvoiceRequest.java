package com.gstbilling.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {
    
    // Business field (optional - uses default if not provided)
    private Long businessId;
    
    private String businessName;
    
    // Customer fields
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private String customerGstNo;
    private String customerPhone;
    // Invoice fields
    private LocalDate invoiceDate;
    private String status;
    
    // Backward compatibility (optional - used if items list is empty)
    private Double amount;
    
    // New: Invoice items
    private List<InvoiceItemDto> items;
}