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
import org.openlmis.cce.domain.BackupGeneratorStatus;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.domain.ManualTemperatureGaugeType;
import org.openlmis.cce.domain.ReasonNotWorkingOrNotInUse;
import org.openlmis.cce.domain.Utilization;
import org.openlmis.cce.domain.VoltageRegulatorStatus;
import org.openlmis.cce.domain.VoltageStabilizerStatus;
import java.time.ZonedDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InventoryItemDto extends BaseDto
    implements InventoryItem.Exporter, InventoryItem.Importer {

  private UUID facilityId;

  private UUID catalogItemId;

  private UUID programId;

  private String uniqueId;

  private String equipmentTrackingId;

  private String barCode;

  private Integer yearOfInstallation;

  private Integer yearOfWarrantyExpiry;

  private String source;

  private FunctionalStatus functionalStatus;

  private Boolean requiresAttention;

  private ReasonNotWorkingOrNotInUse reasonNotWorkingOrNotInUse;

  private Utilization utilization;

  private VoltageStabilizerStatus voltageStabilizer;

  private BackupGeneratorStatus backupGenerator;

  private VoltageRegulatorStatus voltageRegulator;

  private ManualTemperatureGaugeType manualTemperatureGauge;

  private String remoteTemperatureMonitorId;

  private String additionalNotes;

  private ZonedDateTime modifiedDate;

  private UUID lastModifier;

}
