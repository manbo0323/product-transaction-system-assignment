-- liquibase formatted sql
-- changeset Manbo:create t_merchant_account table
-- comment: CREATE TABLE t_merchant_account

DROP TABLE IF EXISTS "t_merchant_account";
CREATE TABLE "t_merchant_account"
(
    "id"         INT AUTO_INCREMENT PRIMARY KEY,
    "merchant_id"    INT NOT NULL,
    "currency"   VARCHAR(3)     NOT NULL DEFAULT 'USD',
    "balance"    DECIMAL(10, 2) NOT NULL DEFAULT 0,
    "created_at" TIMESTAMP      NOT NULL DEFAULT NOW(),
    "updated_at" TIMESTAMP      NOT NULL DEFAULT NOW(),
    UNIQUE ("merchant_id", "currency")
);