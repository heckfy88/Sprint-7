--liquibase formatted sql

--changeset ruslanagaev:init

CREATE INDEX id_ver ON account1 (id, version);