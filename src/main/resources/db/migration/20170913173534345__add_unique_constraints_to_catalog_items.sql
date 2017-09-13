ALTER TABLE ONLY cce_catalog_items
    ADD CONSTRAINT unq_catalog_items_man_model UNIQUE (manufacturer, model);

ALTER TABLE ONLY cce_catalog_items
    ADD CONSTRAINT unq_catalog_items_eqcode UNIQUE (equipmentCode, model);