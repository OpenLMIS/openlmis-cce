CREATE TABLE cce_alerts (
    id uuid NOT NULL PRIMARY KEY,
    type text NOT NULL,
    inventoryitemid uuid NOT NULL,
    starttimestamp timestamptz NOT NULL,
    endtimestamp timestamptz,
    dismissed bool,
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
