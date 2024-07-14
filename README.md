# Product Transaction System

## Overview

This project is a product transaction system designed with a microservices architecture using Spring Boot, Spring Cloud, and Docker. The system is divided into multiple services:
- `user-service`: Manages user accounts and prepaid cash accounts.
- `merchant-service`: Manages merchant inventory and products.
- `api-gateway`: Routes requests to the appropriate services.

Users can purchase products from the merchant's inventory using their prepaid cash accounts. Merchants can add products to their inventory and reconcile the value of sold products with their account balance daily.

## Prerequisites

- Java 21 or higher installed.

## Setup and Running

1. **Clone the repository**:

```bash
git clone <repository-url>
cd product-transaction-system
