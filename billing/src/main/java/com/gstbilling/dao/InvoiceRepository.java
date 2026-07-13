package com.gstbilling.dao;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gstbilling.models.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);
    List<Invoice> findByCustomerNameContainingIgnoreCase(String customerName);
    List<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByStatus(String status);
    

  
}
