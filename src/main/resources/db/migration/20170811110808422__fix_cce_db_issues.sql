ALTER TABLE ONLY cce_inventory
    RENAME TO cce_inventory_items;

ALTER TABLE ONLY cce_catalog
    RENAME TO cce_catalog_items;

ALTER TABLE ONLY cce_inventory_items
    ADD CONSTRAINT fk_inventory_catalog FOREIGN KEY (catalogitemid) REFERENCES cce_catalog_items(id);

ALTER TABLE ONLY cce_inventory_items
    ADD CONSTRAINT unq_inventory_catalog_eqid UNIQUE (catalogitemid, equipmenttrackingid);
