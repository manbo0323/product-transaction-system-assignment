-- liquibase formatted sql
-- changeset Manbo:create t_user_account table
-- comment: CREATE TABLE t_user_account

DROP TABLE IF EXISTS "t_user_account";
CREATE TABLE "t_user_account"
(
    "id"         INT AUTO_INCREMENT PRIMARY KEY,
    "user_id"    INT NOT NULL,
    "currency"   VARCHAR(3)     NOT NULL DEFAULT 'USD',
    "balance"    DECIMAL(10, 2) NOT NULL DEFAULT 0,
    "created_at" TIMESTAMP      NOT NULL DEFAULT NOW(),
    "updated_at" TIMESTAMP      NOT NULL DEFAULT NOW(),
    UNIQUE ("user_id", "currency")
);