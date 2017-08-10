ALTER TABLE ONLY cce_inventory
    ADD COLUMN decommissionDate date,
    ADD COLUMN remoteTemperatureMonitor text NOT NULL,
    DROP COLUMN serialNumber;