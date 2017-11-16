package org.openlmis.cce;

import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.Dimensions;
import org.openlmis.cce.domain.EnergySource;
import org.openlmis.cce.domain.StorageTemperature;

import java.util.UUID;

public class CatalogItemDataBuilder {
  private UUID id = UUID.randomUUID();
  private Boolean fromPqsCatalog = true;
  private String equipmentCode;
  private String type = "type";
  private String model = "model";
  private String manufacturer = "manufacturer";
  private EnergySource energySource = EnergySource.ELECTRIC;
  private Integer dateOfPrequal;
  private StorageTemperature storageTemperature = StorageTemperature.MINUS10;
  private Integer maxOperatingTemp;
  private Integer minOperatingTemp;
  private String energyConsumption;
  private Integer holdoverTime;
  private Integer grossVolume;
  private Integer netVolume;
  private Dimensions dimensions;
  private Boolean visibleInCatalog;
  private Boolean archived = false;

  public CatalogItemDataBuilder withGasolineEnergySource() {
    energySource = EnergySource.GASOLINE;
    return this;
  }

  public CatalogItemDataBuilder withMinusThreeStorageTemperature() {
    storageTemperature = StorageTemperature.MINUS3;
    return this;
  }

  public CatalogItemDataBuilder withArchiveFlag() {
    archived = true;
    return this;
  }

  public CatalogItemDataBuilder withoutFromPqsCatalogFlag() {
    fromPqsCatalog = false;
    return this;
  }

  public CatalogItem build() {
    CatalogItem catalog = new CatalogItem(
        fromPqsCatalog, equipmentCode, type, model, manufacturer, energySource, dateOfPrequal,
        storageTemperature, maxOperatingTemp, minOperatingTemp, energyConsumption, holdoverTime,
        grossVolume, netVolume, dimensions, visibleInCatalog, archived
    );
    catalog.setId(id);

    return catalog;
  }
}
