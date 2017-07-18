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

import static org.openlmis.cce.web.upload.processor.CsvCellProcessors.BOOLEAN_TYPE;
import static org.openlmis.cce.web.upload.processor.CsvCellProcessors.DIMENSIONS_TYPE;
import static org.openlmis.cce.web.upload.processor.CsvCellProcessors.ENERGY_SOURCE_TYPE;
import static org.openlmis.cce.web.upload.processor.CsvCellProcessors.INT_FROM_DOUBLE_TYPE;
import static org.openlmis.cce.web.upload.processor.CsvCellProcessors.STORAGE_TEMPERATURE_TYPE;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.TypeName;
import org.openlmis.cce.web.upload.model.ImportField;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@NoArgsConstructor
@Getter
@Setter
@Entity
@TypeName("Catalog")
@Table(name = "cce_catalog")
@EqualsAndHashCode(callSuper = true)
public class CatalogItem extends BaseEntity {

  public static final String VISIBLE_IN_CATALOG = "Visible in catalog";
  public static final String HOLDOVER_TIME_HOURS = "Holdover time (hours)";
  public static final String ENERGY_CONSUMPTION_NA_FOR_SOLAR = "Energy consumption (NA for solar)";
  public static final String MIN_OPERATING_TEMP_DEGREES_C = "Min operating temp (degrees C)";
  public static final String MAX_OPERATING_TEMP_DEGREES_C = "Max operating temp (degrees C)";
  public static final String STORAGE_TEMPERATURE = "Storage Temperature";
  public static final String DATE_OF_PREQUAL = "Date of prequal";
  public static final String ENERGY_SOURCE = "Energy source";
  public static final String MANUFACTURER = "Manufacturer";
  public static final String MODEL = "Model";
  public static final String TYPE = "Type";
  public static final String PQS_EQUIPMENT_CODE = "PQS equipment code";
  public static final String FROM_PQS_CATALOG = "From PQS catalog";
  public static final String DIMENSIONS = "Dimensions";

  @Column(nullable = false)
  @ImportField(name = FROM_PQS_CATALOG, type = BOOLEAN_TYPE, mandatory = true)
  private boolean fromPqsCatalog;

  @Column(columnDefinition = TEXT)
  @ImportField(name = PQS_EQUIPMENT_CODE)
  private String equipmentCode;

  @Column(columnDefinition = TEXT, nullable = false)
  @ImportField(name = TYPE, mandatory = true)
  private String type;

  @Column(columnDefinition = TEXT, nullable = false)
  @ImportField(name = MODEL, mandatory = true)
  private String model;

  @Column(columnDefinition = TEXT, nullable = false)
  @ImportField(name = MANUFACTURER, mandatory = true)
  private String manufacturer;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @ImportField(name = ENERGY_SOURCE, type = ENERGY_SOURCE_TYPE, mandatory = true)
  private EnergySource energySource;

  @ImportField(name = DATE_OF_PREQUAL, type = INT_FROM_DOUBLE_TYPE)
  private Integer dateOfPrequal;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @ImportField(name = STORAGE_TEMPERATURE, type = STORAGE_TEMPERATURE_TYPE, mandatory = true)
  private StorageTemperature storageTemperature;

  @ImportField(name = MAX_OPERATING_TEMP_DEGREES_C, type = INT_FROM_DOUBLE_TYPE)
  private Integer maxOperatingTemp;

  @ImportField(name = MIN_OPERATING_TEMP_DEGREES_C, type = INT_FROM_DOUBLE_TYPE)
  private Integer minOperatingTemp;

  @Column(columnDefinition = TEXT)
  @ImportField(name = ENERGY_CONSUMPTION_NA_FOR_SOLAR)
  private String energyConsumption;

  @ImportField(name = HOLDOVER_TIME_HOURS, type = INT_FROM_DOUBLE_TYPE)
  private Integer holdoverTime;

  private Integer grossVolume;

  private Integer netVolume;

  @Embedded
  @ImportField(name = DIMENSIONS, type = DIMENSIONS_TYPE)
  private Dimensions dimensions;

  @ImportField(name = VISIBLE_IN_CATALOG, type = BOOLEAN_TYPE)
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
    catalogItem.dimensions = importer.getDimensions();
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
    exporter.setDimensions(dimensions);
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

    void setDimensions(Dimensions width);

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

    Dimensions getDimensions();

    Boolean getVisibleInCatalog();
  }
}
