# Aid Distribution Management System

This project is a full-stack web application built using **Spring Boot** for the backend and **Thymeleaf** for the frontend. It allows donors to list available donation items and organizations to list required items, enabling a seamless donation-matching process. The system also tracks points for donors and logs all donation activities.

---

## Setup Instructions

Before running the project, rename the root folder to demo.

This is necessary because the Java package structure is com.example.demo and Spring Boot expects the root folder name to match the Maven artifact ID.

---

## Key Features

### For Donors:
- Login / Signup
- Add donation items
- View and update donation inventory
- Match with organizations based on need
- Confirm donations with validation
- View donation logs
- Earn and redeem donation points

### For Organizations:
- Login / Signup
- List item requirements
- View incoming donations
- Analyze donations via a Google Charts visualization

---

##  Design Principles & Patterns Followed

###  Architecture Pattern:
**Model-View-Controller (MVC)**  
- Controller: Handles user requests and session logic  
- View: HTML + Thymeleaf templates  
- Model: Java Entity Classes mapped to DB tables  

###  Design Principles:
- **SRP (Single Responsibility Principle)**: Every controller and class has one job.
- **OCP (Open/Closed Principle)**: New features like points and graphs were added without modifying existing logic.
- **GRASP - Controller**: Each controller acts as the system entry point for user actions.
- **GRASP - Low Coupling**: Controllers use repositories and services with minimal dependencies.

###  Design Patterns:
- **Flyweight Pattern**: Shared `DonationItemType` objects to avoid repeated memory use for common type-subtype pairs.
- **Chain of Responsibility (CoR)**: Used in the donation flow for sequential validation (existence, quantity, subtype validity).

---

