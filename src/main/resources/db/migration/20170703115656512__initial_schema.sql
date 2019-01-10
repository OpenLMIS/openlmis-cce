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