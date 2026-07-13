package com.gstbilling.service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.gstbilling.dao.CustomerRepository;
import com.gstbilling.dao.InvoiceRepository;
import com.gstbilling.dto.InvoiceRequest;
import com.gstbilling.helper.ReportGenerator;
import com.gstbilling.models.BusinessDetails;
import com.gstbilling.models.Customer;
import com.gstbilling.models.Invoice;
import com.gstbilling.models.InvoiceItem;
import org.springframework.data.domain.Pageable;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private  ReportGenerator reportGenerator;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private BusinessDetailsService businessDetailsService;

    public Page<Invoice> getAllInvoices(int page , int size){
        Pageable pageable = PageRequest.of(page, size);
        return invoiceRepository.findAll(pageable);
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
    }

    public Invoice createInvoice(InvoiceRequest request) {
        // Get business details first (required for invoice)
        BusinessDetails business = businessDetailsService.getBusinessDetailsByName(request.getBusinessName());
        
        // Get or create customer
        Customer customer = customerRepository
            .findByEmail(request.getCustomerEmail())
            .orElse(Customer.builder()
                .name(request.getCustomerName())
                .email(request.getCustomerEmail())
                .phone(request.getCustomerPhone())
                .address(request.getCustomerAddress())
                .gstin(request.getCustomerGstNo())
                .build());

        
        customer = customerRepository.save(customer);

        // Generate invoice number
        long count = invoiceRepository.count() + 1;
        String invoiceNumber = String.format("%06d", count);

        // Create invoice with business details
        Invoice invoice = Invoice.builder()
            .invoiceNumber(invoiceNumber)
            .business(business)  // Add business details
            .customer(customer)
            .invoiceDate(request.getInvoiceDate())
            .status(request.getStatus())
            .build();

        // Add invoice items if present
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            request.getItems().forEach(itemDto -> {
                InvoiceItem item = InvoiceItem.builder()
                    .itemName(itemDto.getItemName())
                    .quantity(itemDto.getQuantity())
                    .price(itemDto.getPrice())
                    .HSN(itemDto.getHsn())
                    .gstRate(itemDto.getGstRate())
                    .build();
                
           
                // Calculate item totals
                double itemSubtotal = item.getQuantity() * item.getPrice();
                item.setGstAmount(itemSubtotal * item.getGstRate() / 100);
                item.setTotalWithGst(itemSubtotal + item.getGstAmount());
                
                invoice.addItem(item);
            });
            
            // Calculate invoice totals from items
            invoice.calculateTotals();
        } else {
            // Fallback: if no items, use amount from request (backward compatibility)
            double gst = request.getAmount() * 0.18;
            invoice.setSubtotal(request.getAmount());
            invoice.setGstAmount(gst);
            invoice.setTotalAmount(request.getAmount() + gst);
        }

        return invoiceRepository.save(invoice);
    }

    public Invoice updateInvoice(Long id, InvoiceRequest request) {
        Invoice existing = getInvoiceById(id);

        // Update business details if businessId is provided in request
        if (request.getBusinessId() != null) {
            BusinessDetails business = businessDetailsService.getBusinessDetailsById(request.getBusinessId());
            existing.setBusiness(business);
        }

        // Update customer
        Customer customer = existing.getCustomer();
        customer.setName(request.getCustomerName());
        customer.setEmail(request.getCustomerEmail());
        customer.setPhone(request.getCustomerPhone());
        customer.setAddress(request.getCustomerAddress());
        customer.setGstin(request.getCustomerGstNo());
        customerRepository.save(customer);

        // Update invoice basic info
        existing.setInvoiceDate(request.getInvoiceDate());
        existing.setStatus(request.getStatus());

        // Update items if present
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            // Clear existing items
            existing.getItems().clear();
            
            // Add new items
            request.getItems().forEach(itemDto -> {
                InvoiceItem item = InvoiceItem.builder()
                    .itemName(itemDto.getItemName())
                    .quantity(itemDto.getQuantity())
                    .price(itemDto.getPrice())
                    .HSN(itemDto.getHsn())
                    .gstRate(itemDto.getGstRate())
                    .build();
                
                // Calculate item totals
                double itemSubtotal = item.getQuantity() * item.getPrice();
                item.setGstAmount(itemSubtotal * item.getGstRate() / 100);
                item.setTotalWithGst(itemSubtotal + item.getGstAmount());
                
                existing.addItem(item);
            });
            
            // Recalculate invoice totals
            existing.calculateTotals();
        } else {
            // Fallback: use amount from request (backward compatibility)
            double gst = request.getAmount() * 0.18;
            existing.setSubtotal(request.getAmount());
            existing.setGstAmount(gst);
            existing.setTotalAmount(request.getAmount() + gst);
        }

        return invoiceRepository.save(existing);
    }

    public void deleteInvoice(Long id) {
        Invoice existing = getInvoiceById(id);
        invoiceRepository.delete(existing);
    }

    public void sendInvoiceEmail(Long id) {
        // Fetch the full invoice object
        Invoice invoice = getInvoiceById(id);
        if (invoice == null) {
            throw new RuntimeException("Invoice not found for ID: " + id);
        }

        // --- CORE CHANGE IS HERE ---
        // Step 2: Use the ReportGenerator to create the actual PDF byte array
        byte[] pdfAttachment = reportGenerator.generatePdf(invoice);

        // Step 3: Check if the PDF was created successfully
        if (pdfAttachment == null) {
            throw new RuntimeException("Failed to generate PDF for invoice: " + invoice.getInvoiceNumber());
        }

        // Step 4: Send the email with the correctly generated PDF attachment
        emailService.sendInvoice(invoice.getCustomer().getEmail(), pdfAttachment);
    }


    
    public byte[] generateReport(String type) {
        LocalDate now = LocalDate.now();
        LocalDate startDate;

        switch (type.toLowerCase()) {
            case "monthly":
                startDate = now.withDayOfMonth(1);
                break;
            case "quarterly":
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                startDate = LocalDate.of(now.getYear(), (currentQuarter - 1) * 3 + 1, 1);
                break;
            case "annually":
                startDate = LocalDate.of(now.getYear(), 1, 1);
                break;
            default:
                throw new IllegalArgumentException("Invalid report type: " + type);
        }

        List<Invoice> invoices = invoiceRepository.findByInvoiceDateBetween(startDate, now);
        return reportGenerator.generatePdf(invoices, type);
    }
    public byte[] generateCustomReport(String startDateStr, String endDateStr) {
        // Clean up input
        startDateStr = startDateStr.trim();
        endDateStr = endDateStr.trim();

        // Support multiple date formats (e.g., 1-3-2025 or 01-03-2025)
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("[d-M-yyyy][dd-MM-yyyy][yyyy-MM-dd]")
                .toFormatter();

        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);

        List<Invoice> invoices = invoiceRepository.findByInvoiceDateBetween(startDate, endDate);

        String type = "custom_" + startDate + "_to_" + endDate;
        return reportGenerator.generatePdf(invoices, type);
    }

}