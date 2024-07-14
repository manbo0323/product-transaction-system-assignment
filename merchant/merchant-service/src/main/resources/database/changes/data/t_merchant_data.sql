-- liquibase formatted sql
-- changeset Manbo:insert t_merchant, t_merchant_account, t_merchant_product table
-- comment: INSERT INTO t_merchant, t_merchant_account, t_merchant_product

INSERT INTO "t_merchant" ("name", "email", "status", "created_at", "updated_at")
VALUES ('T-SHIRT Shop', 't.shirt@shop.com', 'ACTIVE', NOW(), NOW()),
       ('SHOES Shop', 'shoes@shop.com', 'ACTIVE', NOW(), NOW());

INSERT INTO "t_merchant_account" ("merchant_id", "currency", "balance", "created_at", "updated_at")
VALUES (1, 'USD', 1000.00, NOW(), NOW()),
       (1, 'TWD', 100000.00, NOW(), NOW()),
       (2, 'USD', 1500.00, NOW(), NOW()),
       (2, 'TWD', 200000.00, NOW(), NOW());

INSERT INTO "t_merchant_product" ("merchant_id", "sku", "name", "price", "currency", "quantity", "status", "created_at",
                                  "updated_at")
VALUES (1, 'HOODIE-BLUE-S', 'hoodie coat', 6.00, 'USD', 10, 'ACTIVE', NOW(), NOW()),
       (1, 'TSHIRT-BLACK-L', 'long sleeve T-shirts', 3.00, 'USD', 15, 'ACTIVE', NOW(), NOW()),
       (1, 'TSHIRT-BLACK-XL', 'short sleeve T-shirts', 2.50, 'USD', 30, 'ACTIVE', NOW(), NOW()),
       (2, 'SHOE-BLACK-M', 'black shoes(M)', 15.50, 'USD', 30, 'ACTIVE', NOW(), NOW()),
       (2, 'SHOE-BLACK-L', 'black shoes(L)', 15.50, 'USD', 5, 'ACTIVE', NOW(), NOW());