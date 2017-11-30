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

import static org.openlmis.cce.service.ResourceNames.FACILITIES;
import static org.openlmis.cce.service.ResourceNames.PROGRAMS;
import static org.openlmis.cce.service.ResourceNames.USERS;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.openlmis.cce.domain.BackupGeneratorStatus;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.domain.ManualTemperatureGaugeType;
import org.openlmis.cce.domain.ReasonNotWorkingOrNotInUse;
import org.openlmis.cce.domain.RemoteTemperatureMonitorType;
import org.openlmis.cce.domain.Utilization;
import org.openlmis.cce.domain.VoltageRegulatorStatus;
import org.openlmis.cce.domain.VoltageStabilizerStatus;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "serviceUrl")
public class InventoryItemDto extends BaseDto
    implements InventoryItem.Exporter, InventoryItem.Importer {

  @Setter
  private String serviceUrl;

  @Setter
  @Getter
  private ObjectReferenceDto facility;

  @Getter
  private CatalogItemDto catalogItem;

  @Setter
  @Getter
  private ObjectReferenceDto program;

  @Setter
  @Getter
  private String equipmentTrackingId;

  @Setter
  @Getter
  private String referenceName;

  @Setter
  @Getter
  private Integer yearOfInstallation;

  @Setter
  @Getter
  private Integer yearOfWarrantyExpiry;

  @Setter
  @Getter
  private String source;

  @Setter
  @Getter
  private FunctionalStatus functionalStatus;

  @Setter
  @Getter
  private ReasonNotWorkingOrNotInUse reasonNotWorkingOrNotInUse;

  @Setter
  @Getter
  private Utilization utilization;

  @Setter
  @Getter
  private VoltageStabilizerStatus voltageStabilizer;

  @Setter
  @Getter
  private BackupGeneratorStatus backupGenerator;

  @Setter
  @Getter
  private VoltageRegulatorStatus voltageRegulator;

  @Setter
  @Getter
  private ManualTemperatureGaugeType manualTemperatureGauge;

  @Setter
  @Getter
  private RemoteTemperatureMonitorType remoteTemperatureMonitor;

  @Setter
  @Getter
  private String remoteTemperatureMonitorId;

  @Setter
  @Getter
  private String additionalNotes;

  @Setter
  @Getter
  private LocalDate decommissionDate;

  @Setter
  @Getter
  private ZonedDateTime modifiedDate;

  @Setter
  @Getter
  private UserObjectReferenceDto lastModifier;

  @Override
  public void setCatalogItem(CatalogItem catalogItem) {
    if (catalogItem == null) {
      this.catalogItem = null;
    } else {
      this.catalogItem = new CatalogItemDto();
      catalogItem.export(this.catalogItem);
    }
  }

  @Override
  public void setFacilityId(UUID facilityId) {
    this.facility = ObjectReferenceDto.create(facilityId, serviceUrl, FACILITIES);
  }

  @Override
  public void setProgramId(UUID programId) {
    this.program = ObjectReferenceDto.create(programId, serviceUrl, PROGRAMS);
  }

  @JsonIgnore
  public void setLastModifierId(UUID lastModifierId) {
    this.lastModifier = UserObjectReferenceDto.create(lastModifierId, serviceUrl, USERS);
  }

  @JsonIgnore
  @Override
  public UUID getFacilityId() {
    return facility.getId();
  }

  @JsonIgnore
  @Override
  public UUID getProgramId() {
    return program.getId();
  }

  @Override
  @JsonIgnore
  public UUID getLastModifierId() {
    return lastModifier.getId();
  }
}
