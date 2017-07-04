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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.EnergySource;
import org.openlmis.cce.domain.StorageTemperature;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CatalogItemDto extends BaseDto implements CatalogItem.Exporter, CatalogItem.Importer {

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

  private Integer width;

  private Integer depth;

  private Integer height;

  private Boolean visibleInCatalog;
}
