-- liquibase formatted sql
-- changeset Manbo:create t_user table
-- comment: CREATE TABLE t_user

DROP TABLE IF EXISTS "t_user";
CREATE TABLE "t_user"
(
    "id"         INT AUTO_INCREMENT PRIMARY KEY,
    "name"       VARCHAR(20)    NOT NULL,
    "email"       VARCHAR(255)    NOT NULL,
    "status"     VARCHAR(10)    NOT NULL DEFAULT 'ACTIVE',
    "created_at" TIMESTAMP      NOT NULL DEFAULT NOW(),
    "updated_at" TIMESTAMP      NOT NULL DEFAULT NOW()
);