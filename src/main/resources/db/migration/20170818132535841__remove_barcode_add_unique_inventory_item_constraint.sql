ALTER TABLE ONLY cce_inventory_items
    DROP COLUMN barCode,
    ADD CONSTRAINT unq_inventory
    UNIQUE(catalogItemId, equipmentTrackingId);