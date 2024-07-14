-- liquibase formatted sql
-- changeset Manbo:create t_merchant table
-- comment: CREATE TABLE t_merchant

DROP TABLE IF EXISTS "t_merchant";
CREATE TABLE "t_merchant"
(
    "id"         INT AUTO_INCREMENT PRIMARY KEY,
    "name"       VARCHAR(20)    NOT NULL,
    "email"       VARCHAR(255)    NOT NULL,
    "status"     VARCHAR(10)    NOT NULL DEFAULT 'ACTIVE',
    "created_at" TIMESTAMP      NOT NULL DEFAULT NOW(),
    "updated_at" TIMESTAMP      NOT NULL DEFAULT NOW()
);
