-- liquibase formatted sql
-- changeset Manbo:create t_merchant_product table
-- comment: CREATE TABLE t_merchant_product

DROP TABLE IF EXISTS "t_merchant_product";
CREATE TABLE "t_merchant_product"
(
    "id"          INT AUTO_INCREMENT PRIMARY KEY,
    "merchant_id" INT            NOT NULL,
    "sku"         VARCHAR(50)    NOT NULL,
    "name"        VARCHAR(50)    NOT NULL,
    "price"       DECIMAL(10, 2) NOT NULL DEFAULT 0,
    "currency"    VARCHAR(3)     NOT NULL DEFAULT 'USD',
    "quantity"    INT            NOT NULL DEFAULT 0,
    "status"      VARCHAR(10)    NOT NULL DEFAULT 'ACTIVE',
    "created_at"  TIMESTAMP      NOT NULL DEFAULT NOW(),
    "updated_at"  TIMESTAMP      NOT NULL DEFAULT NOW(),
    UNIQUE ("merchant_id", "sku")
);