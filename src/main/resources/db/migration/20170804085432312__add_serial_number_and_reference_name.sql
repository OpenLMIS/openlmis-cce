ALTER TABLE ONLY cce_inventory
    ADD COLUMN serialNumber text NOT NULL,
    ADD COLUMN referenceName text NOT NULL;