-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way. 
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

CREATE TABLE cce_alerts (
    id uuid NOT NULL PRIMARY KEY,
    externalid varchar(64) NOT NULL UNIQUE,
    type text NOT NULL,
    inventoryitemid uuid NOT NULL,
    starttimestamp timestamptz NOT NULL,
    endtimestamp timestamptz,
    dismisstimestamp timestamptz,
    active bool,
    FOREIGN KEY (inventoryitemid) REFERENCES cce_inventory_items
);

CREATE INDEX ON cce.cce_alerts (inventoryitemid);

CREATE TABLE cce_alert_status_messages (
    alertid uuid NOT NULL,
    locale text NOT NULL,
    message text,
    PRIMARY KEY (alertid, locale),
    FOREIGN KEY (alertid) REFERENCES cce_alerts
);
