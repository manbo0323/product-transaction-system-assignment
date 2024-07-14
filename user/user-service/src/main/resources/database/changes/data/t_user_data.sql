-- liquibase formatted sql
-- changeset Manbo:insert t_user, t_user_account table
-- comment: INSERT INTO t_user, t_user_account

INSERT INTO "t_user" ("name", "email", "status", "created_at", "updated_at")
VALUES ('Manbo Yu', 'manbo.yu@test.com', 'ACTIVE', NOW(), NOW()),
       ('Jonn Wang', 'john.wang@test.com', 'ACTIVE', NOW(), NOW());

INSERT INTO "t_user_account" ("user_id", "currency", "balance", "created_at", "updated_at")
VALUES (1, 'USD', 75.00, NOW(), NOW()),
       (1, 'TWD', 500.00, NOW(), NOW()),
       (2, 'USD', 5.00, NOW(), NOW()),
       (2, 'TWD', 150.00, NOW(), NOW());