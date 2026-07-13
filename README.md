# GST Billing & Invoice Software

A Spring Boot-based GST Billing & Invoice Management System developed using Java 17, Spring Boot 3.5.5, MySQL, Spring Security, JWT Authentication, Java Mail API, and PDFBox.

This application helps businesses generate GST invoices, manage customer billing, generate financial reports, maintain audit logs, and securely access APIs using JWT authentication.

---

## Features

### User Story 1 – Generate Invoice
- Generate GST invoices
- Add customer details
- Calculate GST (18%)
- Generate Invoice ID
- Store invoice information
- Include GSTIN and customer email

### User Story 2 – View & Sort Invoices
- View all invoices
- Sort by:
  - Customer Name
  - Invoice Date
  - Invoice Amount
- Ascending and Descending order

### User Story 3 – Filter Invoices
Filter invoices using:
- Invoice ID
- Customer Name
- Invoice Date

### User Story 4 – Financial Reports
Generate:
- Monthly Profit Report
- Quarterly Profit Report
- Annual Profit Report

### User Story 5 – Audit Trail
Maintain activity logs for:
- Invoice Creation
- Invoice Updates
- User Activities
- Authentication Events

### User Story 6 – Print & Email Invoice
- Generate PDF Invoice
- Print Invoice
- Send Invoice using Java Mail API

### User Story 7 – JWT Authentication
- Secure REST APIs
- Login Authentication
- JWT Token Generation
- Role-Based Authorization

---

# Technology Stack

| Technology | Version |
|------------|----------|
| Java | 17 |
| Spring Boot | 3.5.5 |
| Maven | 3.x |
| MySQL | 8.x |
| Spring Data JPA | 3.5.5 |
| Spring Security | 3.5.5 |
| JWT | 0.11.5 |
| Java Mail API | Spring Boot Starter Mail |
| Apache PDFBox | 2.0.31 |
| Lombok | Latest |
| Eclipse IDE | Latest |

---

# Project Structure

```
billing
│
├── config
├── controller
├── dao
├── dto
├── helper
├── models
├── service
│
└── GstBillingApplication.java
```

---

# Maven Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Mail
- MySQL Connector/J
- Lombok
- Apache PDFBox
- JWT (jjwt-api, jjwt-impl, jjwt-jackson)
- Spring Boot DevTools
- Spring Boot Test

---

# Prerequisites

- Java 17
- Maven
- MySQL Server
- Eclipse IDE / IntelliJ IDEA
- Git

---

# Installation

### Clone Repository

```bash
git clone https://github.com/yogikh2005/GST_Billing_And_Invoice_Software.git
```

### Navigate to Project

```bash
cd GST_Billing_And_Invoice_Software
```

### Configure Database

Update `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gstbilling
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Build Project

```bash
mvn clean install
```

### Run Application

```bash
mvn spring-boot:run
```

or

Run

```
GstBillingApplication.java
```

---

# REST API Modules

- Authentication
- Customer Management
- Invoice Management
- Report Generation
- Audit Logs
- Email Service

---

# Security

- Spring Security
- JWT Authentication
- Password Encryption
- Secure REST APIs
- Authentication & Authorization

---

# Invoice Features

- Invoice Number
- Customer Name
- GSTIN
- Email
- GST Calculation (18%)
- Total Amount
- PDF Invoice
- Email Invoice

---

# Future Enhancements

- Dashboard Analytics
- Export Excel Reports
- QR Code on Invoice
- Barcode Support
- Payment Gateway Integration
- Docker Deployment
- Cloud Deployment (AWS/Azure)

---

# Author

**Yogiraj Mohan Khaladkar**

Engineering Student | Java Backend Developer

---

# License

This project is developed for educational and learning purposes.
