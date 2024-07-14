-- liquibase formatted sql
-- changeset Manbo:create t_merchant_purchase_log table
-- comment: CREATE TABLE t_merchant_purchase_log

DROP TABLE IF EXISTS "t_merchant_purchase_log";
CREATE TABLE "t_merchant_purchase_log"
(
    "id"             INT AUTO_INCREMENT PRIMARY KEY,
    "merchant_id"    INT            NOT NULL,
    "product_id"     INT            NOT NULL,
    "sold_quantity"  INT            NOT NULL DEFAULT 0,
    "purchase_price" DECIMAL(10, 2) NOT NULL DEFAULT 0,
    "currency"       VARCHAR(3)     NOT NULL DEFAULT 'USD',
    "purchase_at"    TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX purchase_at_idx ON "t_merchant_purchase_log"("purchase_at");