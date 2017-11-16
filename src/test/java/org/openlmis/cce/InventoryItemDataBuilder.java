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

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

public class InventoryItemDataBuilder {

  private UUID id = UUID.randomUUID();
  private UUID facilityId = UUID.randomUUID();
  private CatalogItem catalogItem = new CatalogItemDataBuilder().build();
  private UUID programId = UUID.randomUUID();
  private String equipmentTrackingId = "tracking-id";
  private String referenceName = "reference name";
  private Integer yearOfInstallation = 2010;
  private Integer yearOfWarrantyExpiry = 2020;
  private String source = "source";
  private FunctionalStatus functionalStatus = FunctionalStatus.FUNCTIONING;
  private ReasonNotWorkingOrNotInUse reasonNotWorkingOrNotInUse =
      ReasonNotWorkingOrNotInUse.NOT_APPLICABLE;
  private Utilization utilization = Utilization.ACTIVE;
  private VoltageStabilizerStatus voltageStabilizer = VoltageStabilizerStatus.YES;
  private BackupGeneratorStatus backupGenerator = BackupGeneratorStatus.YES;
  private VoltageRegulatorStatus voltageRegulator = VoltageRegulatorStatus.YES;
  private ManualTemperatureGaugeType manualTemperatureGauge = ManualTemperatureGaugeType.BUILD_IN;
  private RemoteTemperatureMonitorType remoteTemperatureMonitor =
      RemoteTemperatureMonitorType.BUILD_IN;
  private String remoteTemperatureMonitorId = "monitor-id";
  private String additionalNotes = "some additional notes";
  private LocalDate decommissionDate = LocalDate.of(2020, 10, 10);
  private ZonedDateTime modifiedDate =
      ZonedDateTime.parse("2016-12-03T09:15:30Z[UTC]");
  private UUID lastModifierId = UUID.randomUUID();

  public InventoryItemDataBuilder withCatalogItem(CatalogItem newCatalogItem) {
    catalogItem = newCatalogItem;
    return this;
  }

  /**
   * Sets last modifier {@link UUID}.
   */
  public InventoryItemDataBuilder withLastModifierId(UUID lastModifierId) {
    this.lastModifierId = lastModifierId;
    return this;
  }

  /**
   * Sets program {@link UUID}.
   */
  public InventoryItemDataBuilder withProgramId(UUID programId) {
    this.programId = programId;
    return this;
  }

  /**
   * Sets facility {@link UUID}.
   */
  public InventoryItemDataBuilder withFacilityId(UUID facilityId) {
    this.facilityId = facilityId;
    return this;
  }

  /**
   * Builds instance of {@link InventoryItem}.
   */
  public InventoryItem build() {
    InventoryItem inventoryItem =
        new InventoryItem(facilityId, catalogItem, programId, equipmentTrackingId, referenceName,
        yearOfInstallation, yearOfWarrantyExpiry, source, functionalStatus,
        reasonNotWorkingOrNotInUse, utilization, voltageStabilizer, backupGenerator,
        voltageRegulator, manualTemperatureGauge, remoteTemperatureMonitor,
        remoteTemperatureMonitorId, additionalNotes, decommissionDate, modifiedDate,
        lastModifierId);
    inventoryItem.setId(id);
    return inventoryItem;
  }

}
