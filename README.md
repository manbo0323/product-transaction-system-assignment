# Product Transaction System

## Overview

This project is a product transaction system designed with a microservices architecture using Spring Boot, Spring Cloud, and Docker. The system is divided into multiple services:
- `user-service`: Manages user accounts and prepaid cash accounts.
- `merchant-service`: Manages merchant inventory and products.
- `api-gateway`: Routes requests to the appropriate services.

Users can purchase products from the merchant's inventory using their prepaid cash accounts. Merchants can add products to their inventory and reconcile the value of sold products with their account balance daily.

## Prerequisites

- Java 21 or higher installed.
- Maven installed.

## Setup and Running

1. **Clone the repository**:

```bash
git clone git@github.com:manbo0323/product-transaction-system-assignment.git
cd product-transaction-system-assignment
```
2. **Maven Package**:

```bash
mvn clean package

### run merchant-service
cd merchant/merchant-service
mvn spring-boot:run

### run user-service
cd user/user-service
mvn spring-boot:run

### run api-gateway
cd api-gateway
mvn spring-boot:run

```

## API Endpoints

**API Gateway: http://localhost:8080/api**:

**user-service**

- POST /users/list
- PUT /users/{userId}/recharge
- PUT /users/{userId}/purchase

**merchant-service**

- POST /merchants/list
- POST /merchants/products/list
- POST /merchants/{merchantId}/products
- GET /merchants/{merchantId}/products
- POST /merchants/{merchantId}/products/{productId}/place-order

