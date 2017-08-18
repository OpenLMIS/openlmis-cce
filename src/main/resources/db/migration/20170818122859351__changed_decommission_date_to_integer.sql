ALTER TABLE ONLY cce_inventory_items
    ADD COLUMN decommissionYear integer,
    DROP COLUMN decommissionDate;