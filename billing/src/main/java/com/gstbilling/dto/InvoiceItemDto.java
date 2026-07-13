package com.gstbilling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for transferring invoice item data between API and service layer
 * This is used inside InvoiceRequest to represent line items
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemDto {
    
    private String itemName;      // e.g., "Website Design", "Backend Development"
    private Integer quantity;     // e.g., 1, 2, 5
    private Double price;  // Per unit price (e.g., 500.0)
    private Integer hsn;
    private Double gstRate;       // GST percentage (e.g., 18.0 for 18%, 12.0 for 12%)
    
    // Note: gstAmount and totalWithGst are calculated in the service layer
    // so they are NOT included in this DTO
}