package com.gstbilling.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    private LocalDate invoiceDate;

    // Business Details (your company info)
    @ManyToOne
    @JoinColumn(name = "business_id")
    private BusinessDetails business;

    // Customer Details
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Invoice Items
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<InvoiceItem> items = new ArrayList<>();

    private Double subtotal; // before GST
    private Double gstAmount; // total GST for all items
    private Double totalAmount; // subtotal + gst
    private String status; // PAID, UNPAID, SENT

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Helper method to add items
    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
    }

    // Helper method to remove items
    public void removeItem(InvoiceItem item) {
        items.remove(item);
        item.setInvoice(null);
    }

    // Helper method to calculate totals from items
    public void calculateTotals() {
        this.subtotal = items.stream()
            .mapToDouble(item -> item.getQuantity() * item.getPrice())
            .sum();
        
        this.gstAmount = items.stream()
            .mapToDouble(InvoiceItem::getGstAmount)
            .sum();
        
        this.totalAmount = this.subtotal + this.gstAmount;
    }
}