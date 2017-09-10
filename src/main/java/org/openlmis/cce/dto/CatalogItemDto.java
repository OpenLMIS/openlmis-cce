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

package org.openlmis.cce.dto;

import static org.openlmis.cce.web.csv.processor.CsvCellProcessors.BOOLEAN_TYPE;
import static org.openlmis.cce.web.csv.processor.CsvCellProcessors.DIMENSIONS_TYPE;
import static org.openlmis.cce.web.csv.processor.CsvCellProcessors.ENERGY_SOURCE_TYPE;
import static org.openlmis.cce.web.csv.processor.CsvCellProcessors.INT_FROM_DOUBLE_TYPE;
import static org.openlmis.cce.web.csv.processor.CsvCellProcessors.STORAGE_TEMPERATURE_TYPE;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.Dimensions;
import org.openlmis.cce.domain.EnergySource;
import org.openlmis.cce.domain.StorageTemperature;
import org.openlmis.cce.web.csv.model.ImportField;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CatalogItemDto extends BaseDto implements CatalogItem.Exporter, CatalogItem.Importer {

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
  public static final String ARCHIVED = "Archived";

  @ImportField(name = FROM_PQS_CATALOG, type = BOOLEAN_TYPE, mandatory = true)
  private Boolean fromPqsCatalog;

  @ImportField(name = PQS_EQUIPMENT_CODE)
  private String equipmentCode;

  @ImportField(name = TYPE, mandatory = true)
  private String type;

  @ImportField(name = MODEL, mandatory = true)
  private String model;

  @ImportField(name = MANUFACTURER, mandatory = true)
  private String manufacturer;

  @ImportField(name = ENERGY_SOURCE, type = ENERGY_SOURCE_TYPE, mandatory = true)
  private EnergySource energySource;

  @ImportField(name = DATE_OF_PREQUAL, type = INT_FROM_DOUBLE_TYPE)
  private Integer dateOfPrequal;

  @ImportField(name = STORAGE_TEMPERATURE, type = STORAGE_TEMPERATURE_TYPE, mandatory = true)
  private StorageTemperature storageTemperature;

  @ImportField(name = MAX_OPERATING_TEMP_DEGREES_C, type = INT_FROM_DOUBLE_TYPE)
  private Integer maxOperatingTemp;

  @ImportField(name = MIN_OPERATING_TEMP_DEGREES_C, type = INT_FROM_DOUBLE_TYPE)
  private Integer minOperatingTemp;

  @ImportField(name = ENERGY_CONSUMPTION_NA_FOR_SOLAR)
  private String energyConsumption;

  @ImportField(name = HOLDOVER_TIME_HOURS, type = INT_FROM_DOUBLE_TYPE)
  private Integer holdoverTime;

  private Integer grossVolume;

  private Integer netVolume;

  @ImportField(name = DIMENSIONS, type = DIMENSIONS_TYPE)
  private Dimensions dimensions;

  @ImportField(name = VISIBLE_IN_CATALOG, type = BOOLEAN_TYPE)
  private Boolean visibleInCatalog;

  @ImportField(name = ARCHIVED, type = BOOLEAN_TYPE, mandatory = true)
  private Boolean archived;

}
