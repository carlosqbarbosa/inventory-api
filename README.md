# Production Inventory System - Backend

A RESTful API built with **Quarkus** for managing production inventory, including products, raw materials, and production planning with intelligent resource allocation.

##  Table of Contents

- [Technologies](#technologies)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Business Logic](#business-logic)
- [Testing](#testing)
- [Project Structure](#project-structure)

---

##  Technologies

- **Java 17+**
- **Quarkus 3.31.2** - Supersonic Subatomic Java Framework
- **PostgreSQL 14+** - Primary database
- **Hibernate ORM with Panache** - Simplified persistence
- **RESTEasy Reactive** - JAX-RS implementation
- **Jackson** - JSON processing
- **Maven** - Build tool

---

##  Features

### Core Functionality
-  **Product Management** - Full CRUD operations for products
-  **Raw Materials Management** - CRUD + stock control (increase/decrease/set)
-  **Bill of Materials (BOM)** - Associate raw materials with products
-  **Production Planning Algorithm** - Intelligent production calculation prioritized by product value
-  **Stock Validation** - Real-time stock availability checking
-  **Low Stock Alerts** - Automatic detection of materials below threshold

### API Endpoints
- RESTful API following best practices
- JSON request/response format
- Proper HTTP status codes
- Error handling and validation

---

##  Prerequisites

- **Java Development Kit (JDK) 17 or higher**
- **Maven 3.8+**
- **PostgreSQL 14+**
- **Git**

---

##  Installation

### 1. Clone the repository

```bash
git clone https://github.com/your-username/inventory-api.git
cd inventory-api
```

### 2. Create PostgreSQL Database

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE inventory_db;

-- Create user (optional)
CREATE USER inventory_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE inventory_db TO inventory_user;
```

### 3. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=your_password
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/inventory_db

# Hibernate Configuration
quarkus.hibernate-orm.database.generation=update
quarkus.hibernate-orm.log.sql=true

# CORS Configuration
quarkus.http.cors=true
quarkus.http.cors.origins=http://localhost:5173
quarkus.http.cors.methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
quarkus.http.cors.headers=accept,authorization,content-type,x-requested-with
quarkus.http.cors.access-control-allow-credentials=true

# Server Port
quarkus.http.port=8080
```

---

##  Running the Application

### Development Mode (with hot reload)

```bash
./mvnw quarkus:dev
```

The application will start at **http://localhost:8080**

### Production Mode

```bash
# Build
./mvnw clean package

# Run
java -jar target/quarkus-app/quarkus-run.jar
```

---

##  API Documentation

### Products

#### Get all products
```http
GET /products
```

#### Get product by ID
```http
GET /products/{id}
```

#### Create product
```http
POST /products
Content-Type: application/json

{
  "name": "Chair",
  "price": 150.00,
  "stock": 10
}
```

#### Update product
```http
PUT /products/{id}
Content-Type: application/json

{
  "name": "Updated Chair",
  "price": 175.00,
  "stock": 15
}
```

#### Delete product
```http
DELETE /products/{id}
```

---

### Raw Materials

#### Get all raw materials
```http
GET /raw-materials
```

#### Get raw material by ID
```http
GET /raw-materials/{id}
```

#### Create raw material
```http
POST /raw-materials
Content-Type: application/json

{
  "name": "Wood",
  "stockQuantity": 100
}
```

#### Update raw material
```http
PUT /raw-materials/{id}
Content-Type: application/json

{
  "name": "Premium Wood",
  "stockQuantity": 120
}
```

#### Delete raw material
```http
DELETE /raw-materials/{id}
```

#### Update stock
```http
PUT /raw-materials/{id}/stock
Content-Type: application/json

{
  "quantity": 50
}
```

#### Increase stock
```http
POST /raw-materials/{id}/stock/increase
Content-Type: application/json

{
  "quantity": 20
}
```

#### Decrease stock
```http
POST /raw-materials/{id}/stock/decrease
Content-Type: application/json

{
  "quantity": 10
}
```

#### Get low stock items
```http
GET /raw-materials/low-stock?threshold=10
```

---

### Product-RawMaterial Associations (BOM)

#### Get product's raw materials
```http
GET /products/{productId}/raw-materials
```

#### Add raw material to product
```http
POST /products/{productId}/raw-materials
Content-Type: application/json

{
  "rawMaterialId": 1,
  "quantityRequired": 2
}
```

#### Update quantity required
```http
PUT /products/{productId}/raw-materials/{rawMaterialId}
Content-Type: application/json

{
  "quantity": 3
}
```

#### Remove raw material from product
```http
DELETE /products/{productId}/raw-materials/{rawMaterialId}
```

---

### Production Plan

#### Get full production plan
```http
GET /production-plan
```

**Response:**
```json
{
  "productionItems": [
    {
      "productId": 1,
      "productName": "Premium Chair",
      "quantity": 50,
      "unitValue": 150.00,
      "totalValue": 7500.00
    },
    {
      "productId": 2,
      "productName": "Basic Table",
      "quantity": 30,
      "unitValue": 200.00,
      "totalValue": 6000.00
    }
  ],
  "totalValue": 13500.00
}
```

#### Get production plan for specific product
```http
GET /production-plan/product/{productId}
```

#### Check if can produce quantity
```http
GET /production-plan/product/{productId}/can-produce?quantity=10
```

**Response:**
```json
{
  "canProduce": true
}
```

---

##  Database Schema

### Tables

#### products
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL |
| price | NUMERIC(10,2) | NOT NULL |
| stock | INTEGER | |

#### raw_materials
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| name | VARCHAR(100) | NOT NULL |
| stock_quantity | INTEGER | NOT NULL |

#### product_raw_materials
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| product_id | BIGINT | NOT NULL, FOREIGN KEY → products(id) |
| raw_material_id | BIGINT | NOT NULL, FOREIGN KEY → raw_materials(id) |
| quantity_required | INTEGER | NOT NULL |

---

##  Business Logic

### Production Planning Algorithm

The system implements an intelligent production planning algorithm that:

1. **Retrieves all products** ordered by price (highest first)
2. **For each product**, calculates the maximum quantity that can be produced based on available raw materials
3. **Allocates raw materials** to higher-value products first
4. **Updates remaining stock** after each allocation
5. **Returns production plan** with total value

**Example:**

Given:
- **Product A**: R$ 200.00, requires 2x Wood, 4x Screws
- **Product B**: R$ 100.00, requires 1x Wood, 2x Screws
- **Stock**: 10x Wood, 20x Screws

Calculation:
1. Product A (higher value) → Can produce 5 units (limited by Wood: 10÷2=5)
2. Allocates: 10 Wood, 20 Screws
3. Remaining: 0 Wood, 0 Screws
4. Product B → Cannot produce (no materials left)

Result: Produce 5x Product A = R$ 1,000.00 total value

---

##  Project Structure

```
inventory-api/
├── src/
│   ├── main/
│   │   ├── java/com/production/
│   │   │   ├── dto/
│   │   │   │   ├── ProductionPlanDTO.java
│   │   │   │   └── ProductionItemDTO.java
│   │   │   ├── entity/
│   │   │   │   ├── Product.java
│   │   │   │   ├── RawMaterial.java
│   │   │   │   └── ProductRawMaterial.java
│   │   │   ├── repository/
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── RawMaterialRepository.java
│   │   │   │   └── ProductRawMaterialRepository.java
│   │   │   ├── resource/
│   │   │   │   ├── ProductResource.java
│   │   │   │   ├── RawMaterialResource.java
│   │   │   │   └── ProductionPlanResource.java
│   │   │   └── service/
│   │   │       ├── ProductService.java
│   │   │       ├── RawMaterialService.java
│   │   │       └── ProductionPlanService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/production/
│           └── [test files]
├── pom.xml
└── README.md
```

---

##  Security Notes

-  **Never commit sensitive credentials** to version control
- Use environment variables for production configuration
- Implement authentication/authorization for production environments
- Enable HTTPS in production

---

##  License

This project is licensed under the MIT License.

---

##  Authors

- **Carlos Barbosa** - Initial work

---

##  Acknowledgments

- Built with [Quarkus](https://quarkus.io/)
- PostgreSQL database
- Inspired by manufacturing resource planning systems

---
