#  EcommerceApp — Spring Boot REST API

A robust, high-performance RESTful backend system built using **Java 25 (JDK 25.0.2)** and **Spring Boot 3.5.16**. This application serves as the production engine for an E-commerce platform, exposing highly structured REST endpoints to handle comprehensive user life cycles, catalog management, real-time inventory adjustments, and complex transactional multi-item order processing.

The project focuses on industry-standard clean code practices, optimized JPA entity relationships, and rigorous transaction boundaries.

---

##  Features

###  User Management
- **Identity Onboarding:** Registration pipeline for creating new system users.
- **Idempotency Check:** Prevents duplicate user registration using database-level email validation.
- **Registry Control:** Endpoints to safely retrieve all registered users or delete existing profiles.

###  Product Management
- **Catalog Injection:** Allows administrators to insert new products into the market catalog.
- **Collision Mitigation:** Blocks duplicate product entries to guarantee data sanity.
- **Inventory Maintenance:** Dynamically updates product specifications, baseline pricing, and stock logs.
- **Absolute Deletion:** Complete removal of product entities from inventory records.

###  Order Management & Workflow
- **Atomic Multi-Item Ordering:** Processes concurrent user orders for multiple products simultaneously.
- **Pre-Transaction Validation:** Asserts customer existence and validates product availability and stock quantity before mutating inventory registers.
- **Financial Compute Engine:** Automatically calculates final multi-item order amounts to generate an invoice-style payload response.
- **State Recovery & Rollbacks:** Safely cancels target orders, updating statuses to `CANCELLED` while executing an automated inventory adjustment to restore exact quantities back to warehouse stock logs.

---

##  System Architecture & Layout

The project strictly follows a standard **Layered Architecture** pattern to guarantee solid separation of concerns and maintainable dependency isolation.

[Client HTTP Request]
│
▼
[REST Controllers]  <── (Intercepts Input / Parses JSON DTOs)
│
▼
[Service Layer]    <── (Executes Strict Transactional Business Rules)
│
▼
[Repository Layer]  <── (Abstraction Powered by Spring Data JPA)
│
▼
[MySQL Database]    <── (Relational Storage Layer)

### Decoupled Operational Layers:
- **Controller Layer (`/controller`):** Handles incoming HTTP requests, sanitizes input parameters, and formats JSON API payloads (`UserController`, `ProductController`, `OrderController`).
- **Service Layer (`/service`):** Houses core business logic, strict validation boundaries, and coordinates transactions (`UserService`, `ProductService`, `OrderService`).
- **Repository Layer (`/repository`):** Performs optimized database CRUD operations abstracting JDBC via Spring Data JPA (`UserRepository`, `ProductRepository`, `OrderRepository`).
- **Entity Layer (`/entity`):** Maps Java objects directly to relational MySQL tables using JPA annotations (`User`, `Product`, `Order`, `OrderProduct`).
- **Model Layer (`/model`):** Contains localized Request/Response DTO payloads (`OrderRequest`, `CartItem`) ensuring data payload structure isolation.

---

##  Relational Database Design & Schema Linkage

User
│
└────────────── One-to-Many
│
Orders
│
One-to-Many
│
OrderProduct (Junction Table)
│
Many-to-One
│
Product


>  **Design Architecture Note:** The `OrderProduct` entity acts as a functional junction table between `Orders` and `Products`. This allows multiple products to be safely associated with a single order transaction while capturing immutable point-of-sale snapshots like historical purchase prices and quantity metrics.

---

##  Core Engineering Problem-Solving & Patches

- **Mitigation of PropertyReferenceException:** Resolved a critical application context crash caused by a method-parsing naming mismatch inside `ProductRepository`. The method `findByProductName(String)` failed because entity properties were structured via snake_case naming constraints (`product_name`). Fixed by dropping automated method tracking and integrating an **explicit JPQL schema query via `@Query` templates** to cleanly route properties without parsing errors.
- **Context Refresh Fault Isolation:** Diagnosed and repaired `UnsatisfiedDependencyException` cascades during application bootstrap. Traced and isolated initialization bean failures back to underlying query parsing bugs in the Data Access layer, stabilizing the global Spring ApplicationContext bootstrap phase.

---

##  REST API Specifications

###  User Routing Module
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/api/user/register` | Register a new user with email validation |
| **GET** | `/api/user/AllUsers` | Retrieve details of all system users |
| **DELETE** | `/api/user/delete/{id}` | Delete an existing user profile by ID |

###  Product Catalog Module
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/api/product/addproduct` | Create/Add a new product to inventory |
| **PUT** | `/api/product/updateinventory/{name}` | Update product information and stock values |
| **DELETE** | `/api/product/deleteProduct/{id}` | Remove an absolute product SKU by ID |

###  Order Management Module
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/api/order/place` | Submit and place a dynamic multi-item order |
| **GET** | `/api/order/{orderId}` | Retrieve structural itemized order details |
| **GET** | `/api/order/user/{userId}` | Track entire targeted user order history |
| **PUT** | `/api/order/cancel/{orderId}` | Cancel active order lifecycle and execute inventory rollback |

---

##  Complete Technology Stack Specifications

- **Language Runtime:** Java 25 (JDK 25.0.2)
- **Framework Core:** Spring Boot 3.5.16 (Spring MVC, Spring Data JPA)
- **Core ORM Framework:** Hibernate ORM 6.6.53.Final
- **Connection Management:** HikariCP 6.3.3 (Zero-overhead connection pooling)
- **Relational Database:** MySQL Server 8.0.46
- **Build Infrastructure:** Apache Maven
- **API Simulation/Testing:** Postman

### 💾 Relational Database Schema Design (DDL Script)

Although Hibernate's `spring.jpa.hibernate.ddl-auto=update` pipeline will automatically synchronize and generate structures upon bootstrap, ensure that the baseline schemas exist inside your local MySQL server instance using the following DDL script:

```sql
CREATE DATABASE IF NOT EXISTS Ecommerce_App_db;
USE Ecommerce_App_db;

-- 1. Base structure mapping to User Entity
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Base structure mapping to Product Entity
CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(150) NOT NULL,
    price DOUBLE NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    description TEXT
) ENGINE=InnoDB;

-- 3. Base structure mapping to Order Entity
CREATE TABLE IF NOT EXISTS orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_name VARCHAR(100),
    total_amount DOUBLE NOT NULL,
    order_status VARCHAR(50) DEFAULT 'PENDING',
    local_date_time DATETIME(6),
    user_id INT NOT NULL, 
    CONSTRAINT FK_orders_user FOREIGN KEY (user_id) 
        REFERENCES users(user_id) 
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- 4. Junction Table mapping to OrderProduct Joint Entity (Many-to-Many Layout)
CREATE TABLE IF NOT EXISTS order_product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price_at_purchase DOUBLE NOT NULL, 
    CONSTRAINT FK_op_order FOREIGN KEY (order_id) 
        REFERENCES orders(order_id) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_op_product FOREIGN KEY (product_id) 
        REFERENCES products(product_id) 
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;