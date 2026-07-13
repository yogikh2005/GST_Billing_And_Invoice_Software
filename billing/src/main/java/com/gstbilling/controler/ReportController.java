package com.gstbilling.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gstbilling.service.InvoiceService;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private InvoiceService invoiceService;


    @GetMapping("/generate/{type}")
    public ResponseEntity<byte[]> generateReport(
            @PathVariable String type,
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        byte[] report;

        if ("custom".equalsIgnoreCase(type) && startDate != null && endDate != null) {
            report = invoiceService.generateCustomReport(startDate, endDate);
        } else {
            report = invoiceService.generateReport(type);
        }

        String fileName = "invoice-report-" + type + "." + format;
        String safeFileName = sanitizeFilename(fileName);

        MediaType mediaType = format.equalsIgnoreCase("pdf") ?
                MediaType.APPLICATION_PDF :
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFileName + "\"")
                .contentType(mediaType)
                .body(report);
    }

    private String sanitizeFilename(String name) {
        return name
                .replaceAll("[\\r\\n]", "")  // remove CR/LF
                .replaceAll("[^a-zA-Z0-9._-]", "_"); // replace any invalid char
    }

}
