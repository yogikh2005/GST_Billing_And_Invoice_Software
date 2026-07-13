package com.gstbilling.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import com.gstbilling.models.BusinessDetails;
import com.gstbilling.models.Customer;
import com.gstbilling.models.Invoice;
import com.gstbilling.models.InvoiceItem;

@Component
public class ReportGenerator {

	// Load Unicode font once
	private PDType0Font loadUnicodeFont(PDDocument document) throws IOException {
		File fontFile = new File("src/main/resources/fonts/NotoSans-Regular.ttf");
		return PDType0Font.load(document, fontFile);
	}

	// Load logo image
	private PDImageXObject loadLogo(PDDocument document, String logoPath) {
		try {
			File logoFile = new File(logoPath);
			if (logoFile.exists()) {
				return PDImageXObject.createFromFileByContent(logoFile, document);
			}
		} catch (IOException e) {
			System.err.println("Could not load logo: " + e.getMessage());
		}
		return null;
	}
	// ... inside ReportGenerator class ...

	// -----------------------------------------------------
	// Entry point for SINGLE invoice (Updated flow)
	// -----------------------------------------------------
	// Entry point for single invoice - FIXED FLOW
	public byte[] generatePdf(Invoice invoice) {
		try (PDDocument document = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);
			PDType0Font font = loadUnicodeFont(document);
			PDImageXObject logo = loadLogo(document, "src/main/resources/images/logo.jpg");

			try (PDPageContentStream content = new PDPageContentStream(document, page)) {
				drawPageBorder(content);
				float margin = 40;
				float yStart = PDRectangle.A4.getHeight() - margin;
				float y = yStart;
				
				// 1. Header (Invoice title, business name, etc.)
				y = addHeader(content, invoice, y, font, logo);
				
				// 2. Business Details ("From") - moved here
				y = addBusinessDetails(content, invoice.getBusiness(), y - 20, font);
				
				// 3. Customer Details ("Billed To")
				y = addCustomerDetails(content, invoice.getCustomer(), y - 20, font);
				
				// 4. Items Table
				y = addItemsTable(content, invoice, y - 20, font);
				
				// 5. Footer (Bank details, T&C)
				addFooter(content, invoice.getBusiness(), 100, font);
			}

			document.save(baos);
			return baos.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private float addBusinessDetails(PDPageContentStream content, BusinessDetails business, float y, PDType0Font font)
	        throws IOException {
	    float leftMargin = 40;
	    y -= 20; // Space after header section

	    writeText2(content, font, 12, leftMargin, y, "From :");
	    y -= 15;

	    // Business Name
	    writeText2(content, font, 10, leftMargin, y, business.getBusinessName());
	    y -= 12;

	    // Address (handle multi-line if needed)
	    if (business.getAddress() != null && !business.getAddress().isEmpty()) {
	        writeText2(content, font, 10, leftMargin, y, business.getAddress());
	        y -= 12;
	    }

	    // City, State, Pincode
	    String location = String.join(", ",
	            business.getCity() != null ? business.getCity() : "",
	            business.getState() != null ? business.getState() : "",
	            business.getPincode() != null ? business.getPincode() : "");
	    if (!location.trim().isEmpty()) {
	        writeText2(content, font, 10, leftMargin, y, location);
	        y -= 12;
	    }

	    // GSTIN
	    if (business.getGstin() != null && !business.getGstin().isEmpty()) {
	        writeText2(content, font, 10, leftMargin, y, "GSTIN: " + business.getGstin());
	        y -= 12;
	    }

	    // Email
	    if (business.getEmail() != null && !business.getEmail().isEmpty()) {
	        writeText2(content, font, 10, leftMargin, y, "Email: " + business.getEmail());
	        y -= 12;
	    }

	    // Phone / Mobile
	    if (business.getMobileNo() != null && !business.getMobileNo().isEmpty()) {
	        writeText2(content, font, 10, leftMargin, y, "Mobile: " + business.getMobileNo());
	        y -= 12;
	    } else if (business.getPhoneNo() != null && !business.getPhoneNo().isEmpty()) {
	        writeText2(content, font, 10, leftMargin, y, "Phone: " + business.getPhoneNo());
	        y -= 12;
	    }

	    // Website
	    if (business.getWebsite() != null && !business.getWebsite().isEmpty()) {
	        writeText2(content, font, 10, leftMargin, y, "Website: " + business.getWebsite());
	        y -= 12;
	    }

	    return y;
	}

	// Entry point for multiple invoices (Reports)
	public byte[] generatePdf(List<Invoice> invoices, String type) {
		if (invoices == null || invoices.isEmpty()) {
			return null;
		}

		try (PDDocument document = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);
			PDType0Font font = loadUnicodeFont(document);
			PDImageXObject logo = loadLogo(document, "src/main/resources/images/logo.jpg");
			BusinessDetails business = invoices.get(0).getBusiness();

			try (PDPageContentStream content = new PDPageContentStream(document, page)) {
				drawPageBorder(content);
				float margin = 40;
				float yStart = PDRectangle.A4.getHeight() - margin;
				float y = yStart;

				// MODIFIED: Use the new Report Header that mimics the invoice header
				y = addReportHeader(content, business, y, font, logo);

				// NEW: Add Report Title (left-aligned) below the header
				y -= 25; // Space after header
				float leftMargin = 40;
				content.setFont(font, 14);
				String reportTitle = type.substring(0, 1).toUpperCase() + type.substring(1) + " Sales Report";
				writeText(content, reportTitle, leftMargin, y, font);

				y -= 14;
				content.setFont(font, 9);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
				String dateText = "Generated on: " + LocalDate.now().format(formatter);
				writeText(content, dateText, leftMargin, y, font);
				// END of NEW Section

				// Summary Statistics
				y = addReportSummary(content, invoices, y - 20, font);

				// Invoice List Table
				y = addInvoiceListTable(content, invoices, y - 30, font);

				// Report Footer
				addReportFooter(content, business, 40, font);
			}

			document.save(baos);
			return baos.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// --------------------------------------------
	// Page Border Method
	// --------------------------------------------
	private void drawPageBorder(PDPageContentStream content) throws IOException {
		float margin = 15;
		float pageWidth = PDRectangle.A4.getWidth();
		float pageHeight = PDRectangle.A4.getHeight();
		content.setLineWidth(2f);
		float x1 = margin;
		float y1 = margin;
		float x2 = pageWidth - margin;
		float y2 = pageHeight - margin;
		content.moveTo(x1, y1);
		content.lineTo(x2, y1);
		content.lineTo(x2, y2);
		content.lineTo(x1, y2);
		content.lineTo(x1, y1);
		content.stroke();
	}

	// --------------------------------------------
	// MODIFIED Report Header Section
	// --------------------------------------------
	private float addReportHeader(PDPageContentStream content, BusinessDetails business, float y, PDType0Font font,
			PDImageXObject logo) throws IOException {
		float leftMargin = 40;
		float rightMargin = 555;

		// Draw header border
		//float headerTop = y + 5;
		float headerBottom = y - 85; // Adjusted height to match invoice header
		// drawLine(content, leftMargin, headerTop, rightMargin, headerTop);
		// drawLine(content, leftMargin, headerBottom, rightMargin, headerBottom);
		// drawLine(content, leftMargin, headerTop, leftMargin, headerBottom);
		// drawLine(content, rightMargin, headerTop, rightMargin, headerBottom);

		// Add logo if available
		if (logo != null) {

			content.drawImage(logo, 35, y - 65, 80, 80);
		}

		// GSTIN at top left
		content.setFont(font, 8);
		writeText(content, "GSTIN: " + business.getGstin(), leftMargin + 5, y - 5, font);

		y -= 38; // Position for centered text

		// Business Name - Bold and centered
		content.setFont(font, 18);
		String businessName = business.getBusinessName();
		float textWidth = font.getStringWidth(businessName) / 1000 * 18;
		writeText(content, businessName, (PDRectangle.A4.getWidth() - textWidth) / 2, y, font);

		y -= 15;

		// Address line
		content.setFont(font, 9);
		String address = String.format("%s, %s, %s - %s", business.getAddress(), business.getCity(),
				business.getState(), business.getPincode());
		textWidth = font.getStringWidth(address) / 1000 * 9;
		writeText(content, address, (PDRectangle.A4.getWidth() - textWidth) / 2, y, font);

		y -= 12;

		// Contact details line
		String contact = String.format("Mob No: %s     Email: %s     H/O Tel.: %s", business.getMobileNo(),
				business.getEmail(), business.getPhoneNo() != null ? business.getPhoneNo() : "N/A");
		textWidth = font.getStringWidth(contact) / 1000 * 9;
		writeText(content, contact, (PDRectangle.A4.getWidth() - textWidth) / 2, y, font);

		// Return y position below the header box
		return headerBottom;
	}

	// --------------------------------------------
	// Report Summary Section
	// --------------------------------------------
	private float addReportSummary(PDPageContentStream content, List<Invoice> invoices, float y, PDType0Font font)
			throws IOException {
		double totalRevenue = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
		double totalGst = invoices.stream().mapToDouble(Invoice::getGstAmount).sum();
		int totalInvoices = invoices.size();

		content.setFont(font, 12);
		writeText(content, "Summary", 40, y, font);
		y -= 18;

		content.setFont(font, 10);
		writeText(content, "Total Invoices: " + totalInvoices, 40, y, font);
		writeText(content, "Total Revenue: ₹" + String.format("%.2f", totalRevenue), 200, y, font);
		writeText(content, "Total GST: ₹" + String.format("%.2f", totalGst), 400, y, font);

		y -= 15;
		drawLine(content, 40, y, 550, y);

		return y;
	}

	

	// -------------------------------------------- // Invoice List Table Section
	private float addInvoiceListTable(PDPageContentStream content, List<Invoice> invoices, float yStart,
			PDType0Font font) throws IOException {
		float tableWidth = 520;
		float x = 40;
		float y = yStart;

		content.setFont(font, 10);
		String[] headers = { "Invoice No", "Date", "Customer", "Subtotal", "GST", "Total" };
		float[] colWidths = { 80, 80, 150, 70, 70, 70 };

		float nextX = x;
		for (int i = 0; i < headers.length; i++) {
			writeText(content, headers[i], nextX + 2, y, font);
			nextX += colWidths[i];
		}

		y -= 15;
		drawLine(content, x, y, x + tableWidth, y);

		double totalSubtotal = 0;
		double totalGst = 0;
		double grandTotal = 0;

		content.setFont(font, 9);
		for (Invoice invoice : invoices) {
			nextX = x;
			y -= 15;
			double subtotal = invoice.getSubtotal();
			double gst = invoice.getGstAmount();
			double total = invoice.getTotalAmount();
			totalSubtotal += subtotal;
			totalGst += gst;
			grandTotal += total;

			writeText(content, invoice.getInvoiceNumber(), nextX + 2, y, font);
			nextX += colWidths[0];
			writeText(content, invoice.getInvoiceDate().toString(), nextX + 2, y, font);
			nextX += colWidths[1];
			String customerName = invoice.getCustomer().getName();
			if (customerName.length() > 20) {
				customerName = customerName.substring(0, 17) + "...";
			}
			writeText(content, customerName, nextX + 2, y, font);
			nextX += colWidths[2];
			writeText(content, String.format("%.2f", subtotal), nextX + 2, y, font);
			nextX += colWidths[3];
			writeText(content, String.format("%.2f", gst), nextX + 2, y, font);
			nextX += colWidths[4];
			writeText(content, String.format("%.2f", total), nextX + 2, y, font);
		}

		y -= 18;
		drawLine(content, x, y, x + tableWidth, y);
		y -= 18;

		content.setFont(font, 10);
		nextX = x;
		writeText(content, "TOTAL", nextX + 2, y, font);
		nextX += colWidths[0] + colWidths[1] + colWidths[2];
		writeText(content, String.format("%.2f", totalSubtotal), nextX + 2, y, font);
		nextX += colWidths[3];
		writeText(content, String.format("%.2f", totalGst), nextX + 2, y, font);
		nextX += colWidths[4];
		writeText(content, String.format("%.2f", grandTotal), nextX + 2, y, font);
		y -= 10;
		drawLine(content, x, y, x + tableWidth, y);

		return y;
	}

	// --------------------------------------------
	// Footer Section
	// --------------------------------------------
	private void addReportFooter(PDPageContentStream content, BusinessDetails business, float y, PDType0Font font)
			throws IOException {
		content.setFont(font, 8);
		writeText(content, "This is a system generated report", 40, y, font);
		y -= 12;
		writeText(content, "For any queries, contact: " + business.getEmail() + " | " + business.getMobileNo(), 40, y,
				font);
	}

	// --------------------------------------------
	// CORRECTED Header / Customer / Items / Footer (Single Invoice)
	// --------------------------------------------

	private float addHeader(PDPageContentStream content, Invoice invoice, float y, PDType0Font font,
			PDImageXObject logo) throws IOException {
		BusinessDetails business = invoice.getBusiness();
		float leftMargin = 40;
		float rightMargin = PDRectangle.A4.getWidth() - 40;
		// Add logo if available
				if (logo != null) {

					content.drawImage(logo, 35, y - 65, 80, 80);
				}
		// Top line: GSTIN and Original Copy
		writeText2(content, font, 9, leftMargin, y, "GSTIN: " + business.getGstin());
		writeTextRight(content, font, 9, rightMargin, y, "Original Copy");
		y -= 15;

		// Centered Title
		writeTextCenter(content, font, 14, y, "TAX INVOICE");
		y -= 22;

		// Centered Business Name
		writeTextCenter(content, font, 18, y, business.getBusinessName());
		y -= 15;

		// Centered Address & Contact
		String address = String.format("%s, %s, %s - %s", business.getAddress(), business.getCity(),
				business.getState(), business.getPincode());
		writeTextCenter(content, font, 9, y, address);
		y -= 12;
		String contact = String.format("Mob No: %s | Email: %s | H/O Tel.: %s", business.getMobileNo(),
				business.getEmail(), business.getPhoneNo() != null ? business.getPhoneNo() : "N/A");
		writeTextCenter(content, font, 9, y, contact);
		y -= 15;

		// Separator line
		drawLine(content, leftMargin, y, rightMargin, y);
		y -= 15;

		// Invoice Details line
		writeText2(content, font, 10, leftMargin, y, "Invoice Number: " + invoice.getInvoiceNumber());
		writeTextCenter(content, font, 10, y, "Date: " + invoice.getInvoiceDate());
		writeTextRight(content, font, 10, rightMargin, y, "Status: " + invoice.getStatus());

		return y;
	}

	private float addCustomerDetails(PDPageContentStream content, Customer customer, float y, PDType0Font font)
			throws IOException {
		float leftMargin = 40;


		writeText2(content, font, 12, leftMargin, y, "Billed To:");
		y -= 15;

		writeText2(content, font, 10, leftMargin, y, customer.getName());
		y -= 12;
		// Handle multi-line address if needed
		writeText2(content, font, 10, leftMargin, y, customer.getAddress());
		y -= 12;
		writeText2(content, font, 10, leftMargin, y, "Email: " + customer.getEmail());
		if (customer.getGstin() != null && !customer.getGstin().isEmpty()) {
			y -= 12;
			writeText2(content, font, 10, leftMargin, y, "GSTIN: " + customer.getGstin());
		}
		return y;
	}

	private float addItemsTable(PDPageContentStream content, Invoice invoice, float yStart, PDType0Font font)
			throws IOException {
		float x = 40;
		float tableTopY = yStart - 20; // Space before table
		float tableBottomY = 150; // Y position for totals, leaving space for footer
		float tableWidth = PDRectangle.A4.getWidth() - (2 * x);

		// Define column properties
		String[] headers = { "Item", "Qty","HSN", "Price", "GST Rate", "GST Amt", "Total" };
		float[] colWidths = { 220, 43,40 ,50, 60, 50, 40 }; // Adjusted widths

		// Draw Table Header
		float y = tableTopY;
		float currentX = x;
		drawLine(content, x, y + 12, x + tableWidth, y + 12); // Line above header
		for (int i = 0; i < headers.length; i++) {
			if (i == 0) { // Left-align first column
				writeText2(content, font, 10, currentX, y, headers[i]);
			} else { // Right-align other headers
				writeTextRight(content, font, 10, currentX + colWidths[i], y, headers[i]);
			}
			currentX += colWidths[i];
		}
		y -= 8;
		drawLine(content, x, y, x + tableWidth, y); // Line below header

		// Draw Table Rows
		if (invoice.getItems() != null) {
			for (InvoiceItem item : invoice.getItems()) {
				y -= 15;
				currentX = x;

				// Item Name (left-aligned)
				writeText2(content, font, 9, currentX, y, item.getItemName());
				currentX += colWidths[0];

				// Qty (right-aligned)
				writeTextRight(content, font, 9, currentX + colWidths[1], y, String.valueOf(item.getQuantity()));
				currentX += colWidths[1];
				
				// HSN (right-aligned)
				writeTextRight(content, font, 9, currentX + colWidths[2], y, String.valueOf(item.getHSN()));
				currentX += colWidths[2];
				
				// Price (right-aligned)
				writeTextRight(content, font, 9, currentX + colWidths[3], y, String.format("%,.2f", item.getPrice()));
				currentX += colWidths[3];

				// GST Rate (right-aligned)
				writeTextRight(content, font, 9, currentX + colWidths[4], y,
						String.format("%.0f%%", item.getGstRate()));
				currentX += colWidths[4];

				// GST Amt (right-aligned)
				writeTextRight(content, font, 9, currentX + colWidths[5], y,
						String.format("%,.2f", item.getGstAmount()));
				currentX += colWidths[5];

				// Total (right-aligned)
				writeTextRight(content, font, 9, currentX + colWidths[6], y,
						String.format("%,.2f", item.getTotalWithGst()));
			}
		}

		// Draw Totals Section at the bottom of the items section
		float totalsX = x + tableWidth; // Right edge of the table
		float totalsLabelX = totalsX - 100; // X position for labels like "Subtotal:"

		drawLine(content, x, y - 10, x + tableWidth, y - 10); // Line above totals
		y = tableBottomY;

		writeTextRight(content, font, 10, totalsLabelX, y, "Subtotal: ");
		writeTextRight(content, font, 10, totalsX, y, "₹" + String.format("%,.2f", invoice.getSubtotal()));
		y -= 15;

		writeTextRight(content, font, 10, totalsLabelX, y, "Total GST: ");
		writeTextRight(content, font, 10, totalsX, y, "₹" + String.format("%,.2f", invoice.getGstAmount()));
		y -= 15;

		writeTextRight(content, font, 10, totalsLabelX, y, "Grand Total: ");
		writeTextRight(content, font, 10, totalsX, y, "₹" + String.format("%,.2f", invoice.getTotalAmount()));

		return y;
	}

	private void addFooter(PDPageContentStream content, BusinessDetails business, float y, PDType0Font font)
			throws IOException {
		float leftMargin = 40;
		float rightMargin = PDRectangle.A4.getWidth() - 40;

		y+=20;
		// Bank Details
		writeText2(content, font, 10, leftMargin, y, "Bank Details");
		y -= 12;
		writeText2(content, font, 9, leftMargin, y,
				"Bank: " + business.getBankName() + ", A/C No: " + business.getAccountNumber());
		y -= 12;
		writeText2(content, font, 9, leftMargin, y,
				"IFSC: " + business.getIfscCode() + ", Branch: " + business.getBranch());

		y -= 20;
		// Terms & Conditions
		if (business.getTermsAndConditions() != null && !business.getTermsAndConditions().isEmpty()) {
			writeText2(content, font, 10, leftMargin, y, "Terms & Conditions:");
			y -= 12;
			String terms = business.getTermsAndConditions();
			String[] lines = terms.split("(?=\\s*[1-9][\\.|\\)])");// splits by newline, comma, or semicolon (you can adjust)

			float fontSize = 8;
			float leading = 0.6f * fontSize;
			float yPosition = y;

			for (String line : lines) {
			    writeText2(content, font, fontSize, leftMargin, yPosition, line.trim());
			    yPosition -= leading; // move down for next line
			}

		}
	}

	// --------------------------------------------
	// Helper Methods
	// --------------------------------------------
	private void writeText(PDPageContentStream content, String text, float x, float y, PDType0Font font)
			throws IOException {
		content.beginText();
		content.newLineAtOffset(x, y);
		content.showText(text);
		content.endText();
	}

	private void drawLine(PDPageContentStream content, float xStart, float yStart, float xEnd, float yEnd)
			throws IOException {
		content.moveTo(xStart, yStart);
		content.lineTo(xEnd, yEnd);
		content.stroke();
	}

	private void writeText2(PDPageContentStream content, PDType0Font font, float fontSize, float x, float y,
			String text) throws IOException {
		content.setFont(font, fontSize);
		content.beginText();
		content.newLineAtOffset(x, y);
		content.showText(text);
		content.endText();
	}

	private void writeTextRight(PDPageContentStream content, PDType0Font font, float fontSize, float x, float y,
			String text) throws IOException {
		float textWidth = font.getStringWidth(text) / 1000 * fontSize;
		writeText2(content, font, fontSize, x - textWidth, y, text);
	}

	private void writeTextCenter(PDPageContentStream content, PDType0Font font, float fontSize, float y, String text)
			throws IOException {
		float textWidth = font.getStringWidth(text) / 1000 * fontSize;
		float x = (PDRectangle.A4.getWidth() - textWidth) / 2;
		writeText2(content, font, fontSize, x, y, text);
	}
}