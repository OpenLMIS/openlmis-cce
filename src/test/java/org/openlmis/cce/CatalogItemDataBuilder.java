/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.cce;

import java.util.UUID;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.Dimensions;
import org.openlmis.cce.domain.EnergySource;
import org.openlmis.cce.domain.StorageTemperature;

@SuppressWarnings({"PMD.TooManyMethods"})
public class CatalogItemDataBuilder {
  private UUID id;
  private Boolean fromPqsCatalog;
  private String equipmentCode;
  private String type;
  private String model;
  private String manufacturer;
  private EnergySource energySource;
  private Integer dateOfPrequal;
  private StorageTemperature storageTemperature;
  private Integer maxOperatingTemp;
  private Integer minOperatingTemp;
  private String energyConsumption;
  private Integer holdoverTime;
  private Integer grossVolume;
  private Integer netVolume;
  private Dimensions dimensions;
  private Boolean visibleInCatalog;
  private Boolean archived;

  /**
   * Returns instance of {@link CatalogItemDataBuilder} with sample data.
   */
  public CatalogItemDataBuilder() {
    id = UUID.fromString("15498136-8972-4ade-9f14-602bf199652b");
    fromPqsCatalog = true;
    type = "type";
    model = "model";
    manufacturer = "manufacturer";
    energySource = EnergySource.ELECTRIC;
    storageTemperature = StorageTemperature.MINUS10;
    archived = false;
  }

  /**
   * Creates new instance of {@link CatalogItem} with provided data without id.
   */
  public CatalogItem buildAsNew() {
    return new CatalogItem(
        fromPqsCatalog, equipmentCode, type, model, manufacturer, energySource, dateOfPrequal,
        storageTemperature, maxOperatingTemp, minOperatingTemp, energyConsumption, holdoverTime,
        grossVolume, netVolume, dimensions, visibleInCatalog, archived
    );
  }

  /**
   * Creates new instance of {@link CatalogItem} with provided data.
   */
  public CatalogItem build() {
    CatalogItem catalog = buildAsNew();
    catalog.setId(id);

    return catalog;
  }

  public CatalogItemDataBuilder withEquipmentCode(String equipmentCode) {
    this.equipmentCode = equipmentCode;
    return this;
  }

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

  public CatalogItemDataBuilder withType(String type) {
    this.type = type;
    return this;
  }

  public CatalogItemDataBuilder withModel(String model) {
    this.model = model;
    return this;
  }

  public CatalogItemDataBuilder withManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public CatalogItemDataBuilder withVisibleInCatalog() {
    this.visibleInCatalog = true;
    return this;
  }

  public CatalogItemDataBuilder withoutVisibleInCatalog() {
    this.visibleInCatalog = false;
    return this;
  }
}
