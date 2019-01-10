-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way. 
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

ALTER TABLE ONLY cce_inventory
    RENAME TO cce_inventory_items;

ALTER TABLE ONLY cce_catalog
    RENAME TO cce_catalog_items;

ALTER TABLE ONLY cce_inventory_items
    ADD CONSTRAINT fk_inventory_catalog FOREIGN KEY (catalogitemid) REFERENCES cce_catalog_items(id);

ALTER TABLE ONLY cce_inventory_items
    ADD CONSTRAINT unq_inventory_catalog_eqid UNIQUE (catalogitemid, equipmenttrackingid);
