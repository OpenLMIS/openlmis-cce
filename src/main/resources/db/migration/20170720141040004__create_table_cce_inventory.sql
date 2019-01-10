-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way. 
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

CREATE TABLE cce_inventory (
    id uuid NOT NULL,
    facilityId uuid NOT NULL,
    catalogItemId uuid NOT NULL,
    programId uuid NOT NULL,
    uniqueId text NOT NULL,
    equipmentTrackingId text,
    barCode text,
    yearOfInstallation integer NOT NULL,
    yearOfWarrantyExpiry integer,
    source text,
    functionalStatus text NOT NULL,
    requiresAttention boolean NOT NULL,
    reasonNotWorkingOrNotInUse text,
    utilization text NOT NULL,
    voltageStabilizer text NOT NULL,
    backupGenerator text NOT NULL,
    voltageRegulator text NOT NULL,
    manualTemperatureGauge text NOT NULL,
    remoteTemperatureMonitorId text,
    additionalNotes text,
    modifieddate timestamp with time zone,
    lastModifier uuid
);