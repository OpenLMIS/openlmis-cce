-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way. 
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

--
-- Name: cce_catalog; Type: TABLE; Schema: cce; Owner: postgres; Tablespace:
--

CREATE TABLE cce_catalog (
    id uuid NOT NULL,
    fromPqsCatalog boolean NOT NULL,
    equipmentCode text,
    type text NOT NULL,
    model text NOT NULL,
    manufacturer text NOT NULL,
    energySource text NOT NULL,
    dateOfPrequal integer,
    storageTemperature text NOT NULL,
    maxOperatingTemp integer,
    minOperatingTemp integer,
    energyConsumption text,
    holdoverTime integer,
    grossVolume integer,
    netVolume integer,
    width integer,
    depth integer,
    height integer,
    visibleInCatalog boolean
);