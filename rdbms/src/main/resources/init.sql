--liquibase formatted sql

--changeset ruslanagaev:init

create table account1
(
    id bigserial constraint account_pk primary key,
    amount bigint constraint positive_amount check (amount>=0),
    version int
);


