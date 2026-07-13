package com.gstbilling.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "invoice_items")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    private String itemName;
    private Integer quantity;
    private Integer HSN;
    private Double price; // per unit price
    private Double gstRate; // GST percentage (e.g., 18.0 for 18%)
    private Double gstAmount; // calculated GST amount
    private Double totalWithGst; // quantity * price + gstAmount

}