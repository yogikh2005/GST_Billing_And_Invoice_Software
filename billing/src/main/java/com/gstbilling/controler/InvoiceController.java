package com.gstbilling.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gstbilling.dto.InvoiceRequest;
import com.gstbilling.models.Invoice;
import com.gstbilling.service.InvoiceService;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
	
    @Autowired
    private InvoiceService invoiceService;
  

    @GetMapping
    public Page<Invoice> getAllInvoices(
    		@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return invoiceService.getAllInvoices(page,size);
    }


    @GetMapping("/{id}")
    public Invoice getInvoice(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    @PostMapping
    public Invoice createInvoice(@RequestBody InvoiceRequest request) {
        return invoiceService.createInvoice(request);
    }

    
    @PutMapping("/{id}")
    public Invoice updateInvoice(@PathVariable Long id, @RequestBody InvoiceRequest request) {
        return invoiceService.updateInvoice(id, request);
    }

  
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok("Invoice deleted successfully!");
    }


    @PostMapping("/{id}/send-email")
    public ResponseEntity<String> sendInvoiceEmail(@PathVariable Long id) {
        invoiceService.sendInvoiceEmail(id);
        return ResponseEntity.ok("Invoice sent via email!");
    }

}
