# 🎟️ BookMyShow – Backend System Design & Architecture Showcase

A **scalable, secure, and cleanly architected backend system** inspired by BookMyShow, built using **Spring Boot**, **PostgreSQL**, and **Hexagonal (Ports & Adapters) Architecture**.

This project is intentionally designed as a **system design + backend engineering showcase**, focusing on:
- Clean architecture
- SOLID principles
- Database normalization
- Transactional integrity
- Real-world booking constraints (seat locking, concurrency, pricing)

---

## 📌 Project Goals

- Design a **real-world ticket booking backend**
- Apply **Hexagonal Architecture** instead of traditional layered MVC
- Ensure **data integrity & concurrency safety**
- Keep the codebase **clean, testable, and extensible**
- Demonstrate **professional backend engineering practices**

---

## 🧠 Architectural Overview

### Hexagonal Architecture (Ports & Adapters)

This project follows **Hexagonal Architecture**, ensuring **business logic is completely isolated from frameworks and infrastructure**.

             ┌────────────────────┐
             │   Controllers      │
             │ (Inbound Adapters) │
             └─────────▲──────────┘
                       │
             ┌─────────┴──────────┐
             │   Application      │
             │     Services       │
             │ (Use Cases / Core) │
             └─────────▲──────────┘
                       │
             ┌─────────┴──────────┐
             │     Domain          │
             │ (Entities + Rules) │
             └─────────▲──────────┘
                       │
             ┌─────────┴──────────┐
             │ Repositories / DB  │
             │ (Outbound Adapter) │
             └────────────────────┘

### Why Hexagonal?

✔ Business logic is **framework-agnostic**  
✔ Database can be swapped without touching core logic  
✔ Easy to test using mocks  
✔ Scales well as complexity grows  

---

## 🧱 SOLID Principles Applied

| Principle | How It’s Used |
|---------|--------------|
| **S – Single Responsibility** | Controllers, Services, Repositories have clear, distinct roles |
| **O – Open/Closed** | New filters, pricing rules, auth methods added without modifying existing logic |
| **L – Liskov Substitution** | Interfaces used consistently (e.g., repositories, auth strategies) |
| **I – Interface Segregation** | Small, focused interfaces instead of fat abstractions |
| **D – Dependency Inversion** | Core services depend on abstractions, not implementations |

---

## 📂 Project Structure (High-Level)

---

## 🗄️ Database Design & Normalization

### Database: **PostgreSQL**

The schema is designed using **strict normalization (3NF)** to avoid:
- Data duplication
- Update anomalies
- Inconsistent state

### Core Tables

| Table | Responsibility |
|-----|---------------|
| `users` | Authentication & authorization |
| `venues` | Physical locations |
| `auditoriums` | Screens inside venues |
| `shows` | Time-bound events |
| `seat_categories` | GOLD / SILVER / PLATINUM |
| `show_seat_pricing` | Dynamic pricing per show |
| `bookings` | Booking metadata |
| `booking_seats` | Seat-level locking |
| `payments` | Payment state tracking |

### Normalization Highlights

- **No derived data stored**
- **No duplicated pricing**
- **Many-to-Many relationships resolved explicitly**
- **Foreign keys enforced**
- **Enum usage limited to bounded domains**

✔ Pricing is separated (`show_seat_pricing`)  
✔ Seats are transactionally locked  
✔ Payments are decoupled from bookings  

---

## 🔐 Authentication & Security

- **JWT-based authentication**
- **Role-based authorization (ADMIN / USER)**
- Passwords are **never stored in plaintext**
- BCrypt hashing with salting
- Token validation centralized in AuthService

---

## 🔄 Booking Flow (ACID-Safe)

1. User selects seats
2. Seats are **locked at DB level**
3. Availability re-verified inside transaction
4. Price calculated dynamically
5. Booking confirmed
6. Payment finalized
7. Seats marked as BOOKED

✔ Prevents double booking  
✔ Handles concurrent requests safely  
✔ Fully transactional  

---

## 🧪 Validation & Error Handling

- DTO-level validation using `@Valid`
- Custom domain exceptions
- Global exception handler
- Clear HTTP status mapping

---

## 📄 Clean Code Practices

- Meaningful naming conventions
- No logic inside controllers
- No entity leakage into API responses
- DTOs used for all external communication
- Builders for immutability
- Lazy loading handled carefully

✔ Easy to read  
✔ Easy to extend  
✔ Easy to review  

---

## 📊 API Documentation

- Swagger/OpenAPI integrated
- Secured endpoints with Bearer authentication
- Clear request/response schemas

---

## 🚀 Tech Stack

- **Java 21**
- **Spring Boot 4.x**
- **Spring Data JPA**
- **PostgreSQL**
- **Hibernate Validator**
- **JWT**
- **Swagger / OpenAPI**
- **Docker (optional)**

---

## 🎯 Why This Project Matters

This is **not a CRUD demo**.

It demonstrates:
- Real-world backend architecture
- Enterprise-grade design decisions
- Concurrency-safe booking systems
- Clean, scalable, and testable code

Ideal for:
- Backend interviews
- System design discussions
- Architecture reviews
- Advanced Spring Boot learning

---

## 👤 Author

**Thirumurugan**  
Backend Engineer | System Design Enthusiast  

🔗 GitHub: https://github.com/THIRU-1074

---

## ⭐ Feedback & Contributions

Feedback, discussions, and architectural suggestions are welcome.
If this project helped you, consider giving it a ⭐️
