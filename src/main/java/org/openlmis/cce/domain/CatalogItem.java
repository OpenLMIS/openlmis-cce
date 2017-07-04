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

package org.openlmis.cce.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.TypeName;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@Entity
@TypeName("Catalog")
@Table(name = "cce_catalog")
public class CatalogItem extends BaseEntity {

  @Column(nullable = false)
  private boolean fromPqsCatalog;

  @Column(columnDefinition = TEXT)
  private String equipmentCode;

  @Column(columnDefinition = TEXT, nullable = false)
  private String type;

  @Column(columnDefinition = TEXT, nullable = false)
  private String model;

  @Column(columnDefinition = TEXT, nullable = false)
  private String manufacturer;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private EnergySource energySource;

  private int dateOfPrequal;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private StorageTemperature storageTemperature;

  private int maxOperatingTemp;

  private int minOperatingTemp;

  @Column(columnDefinition = TEXT)
  private String energyConsumption;

  private int holdoverTime;

  private int grossVolume;

  private int netVolume;

  private int width;

  private int depth;

  private int height;

  private boolean visibleInCatalog;

  /**
   * Creates new instance based on data from {@link Importer}
   *
   * @param importer instance of {@link Importer}
   * @return new instance of CatalogItem.
   */
  public static CatalogItem newInstance(Importer importer) {
    CatalogItem catalogItem = new CatalogItem();

    catalogItem.id = importer.getId();
    catalogItem.fromPqsCatalog = importer.getFromPqsCatalog();
    catalogItem.equipmentCode = importer.getEquipmentCode();
    catalogItem.type = importer.getType();
    catalogItem.model = importer.getModel();
    catalogItem.manufacturer = importer.getManufacturer();
    catalogItem.energySource = importer.getEnergySource();
    catalogItem.dateOfPrequal = importer.getDateOfPrequal();
    catalogItem.storageTemperature = importer.getStorageTemperature();
    catalogItem.maxOperatingTemp = importer.getMaxOperatingTemp();
    catalogItem.minOperatingTemp = importer.getMinOperatingTemp();
    catalogItem.energyConsumption = importer.getEnergyConsumption();
    catalogItem.holdoverTime = importer.getHoldoverTime();
    catalogItem.grossVolume = importer.getGrossVolume();
    catalogItem.netVolume = importer.getNetVolume();
    catalogItem.width = importer.getWidth();
    catalogItem.depth = importer.getDepth();
    catalogItem.height = importer.getHeight();
    catalogItem.visibleInCatalog = importer.getVisibleInCatalog();

    return catalogItem;
  }

  /**
   * Creates list of new instances based on data from {@link Importer} list
   *
   * @param importers {@link Importer} list
   * @return list of new CatalogItem instances.
   */
  public static List<CatalogItem> newInstance(List<? extends Importer> importers) {
    return importers.stream()
        .map(CatalogItem::newInstance)
        .collect(Collectors.toList());
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setId(id);
    exporter.setFromPqsCatalog(fromPqsCatalog);
    exporter.setEquipmentCode(equipmentCode);
    exporter.setType(type);
    exporter.setModel(model);
    exporter.setManufacturer(manufacturer);
    exporter.setEnergySource(energySource);
    exporter.setDateOfPrequal(dateOfPrequal);
    exporter.setStorageTemperature(storageTemperature);
    exporter.setMaxOperatingTemp(maxOperatingTemp);
    exporter.setMinOperatingTemp(minOperatingTemp);
    exporter.setEnergyConsumption(energyConsumption);
    exporter.setHoldoverTime(holdoverTime);
    exporter.setGrossVolume(grossVolume);
    exporter.setNetVolume(netVolume);
    exporter.setWidth(width);
    exporter.setDepth(depth);
    exporter.setHeight(height);
    exporter.setVisibleInCatalog(visibleInCatalog);
  }

  public interface Exporter {
    void setId(java.util.UUID id);

    void setFromPqsCatalog(Boolean fromPqsCatalog);

    void setEquipmentCode(String equipmentCode);

    void setType(String type);

    void setModel(String model);

    void setManufacturer(String manufacturer);

    void setEnergySource(EnergySource energySource);

    void setDateOfPrequal(Integer dateOfPrequal);

    void setStorageTemperature(StorageTemperature storageTemperature);

    void setMaxOperatingTemp(Integer maxOperatingTemp);

    void setMinOperatingTemp(Integer minOperatingTemp);

    void setEnergyConsumption(String energyConsumption);

    void setHoldoverTime(Integer holdoverTime);

    void setGrossVolume(Integer grossVolume);

    void setNetVolume(Integer netVolume);

    void setWidth(Integer width);

    void setDepth(Integer depth);

    void setHeight(Integer height);

    void setVisibleInCatalog(Boolean visibleInCatalog);
  }

  public interface Importer {
    UUID getId();

    Boolean getFromPqsCatalog();

    String getEquipmentCode();

    String getType();

    String getModel();

    String getManufacturer();

    EnergySource getEnergySource();

    Integer getDateOfPrequal();

    StorageTemperature getStorageTemperature();

    Integer getMaxOperatingTemp();

    Integer getMinOperatingTemp();

    String getEnergyConsumption();

    Integer getHoldoverTime();

    Integer getGrossVolume();

    Integer getNetVolume();

    Integer getWidth();

    Integer getDepth();

    Integer getHeight();

    Boolean getVisibleInCatalog();
  }
}
