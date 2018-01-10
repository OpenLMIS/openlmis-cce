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
import org.openlmis.cce.testutil.RandomStringGenerator;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.UUID;

@SuppressWarnings("PMD.TooManyMethods")
public class InventoryItemDataBuilder {

  private UUID id = UUID.fromString("35329395-7fe7-40cf-adc4-89a006746861");
  private UUID facilityId = UUID.fromString("78d42bdb-9150-4c52-bd77-e42d777cfaed");
  private CatalogItem catalogItem = new CatalogItemDataBuilder().build();
  private UUID programId = UUID.fromString("466b2e7f-5798-4027-a2ca-373627748ea6");
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
  private UUID lastModifierId = UUID.fromString("10c5e0e7-2697-4146-8bb9-e34f57f1d156");

  /**
   * Sets {@link CatalogItem}.
   */
  public InventoryItemDataBuilder withCatalogItem(CatalogItem newCatalogItem) {
    catalogItem = newCatalogItem;
    return this;
  }

  /**
   * Sets inventory {@link UUID}.
   */
  public InventoryItemDataBuilder withId(UUID id) {
    this.id = id;
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
   * Sets {@link FunctionalStatus}.
   */
  public InventoryItemDataBuilder withStatus(FunctionalStatus status) {
    this.functionalStatus = status;
    return this;
  }

  public InventoryItemDataBuilder withReasonNotWorkingOrNotInUse(
      ReasonNotWorkingOrNotInUse reasonNotWorkingOrNotInUse) {
    this.reasonNotWorkingOrNotInUse = reasonNotWorkingOrNotInUse;
    return this;
  }

  public InventoryItemDataBuilder withUtilization(Utilization utilization) {
    this.utilization = utilization;
    return this;
  }

  public InventoryItemDataBuilder withVoltageStabilizer(VoltageStabilizerStatus voltageStabilizer) {
    this.voltageStabilizer = voltageStabilizer;
    return this;
  }

  public InventoryItemDataBuilder withBackupGenerator(BackupGeneratorStatus backupGenerator) {
    this.backupGenerator = backupGenerator;
    return this;
  }

  public InventoryItemDataBuilder withVoltageRegulator(VoltageRegulatorStatus voltageRegulator) {
    this.voltageRegulator = voltageRegulator;
    return this;
  }

  public InventoryItemDataBuilder withManualTemperatureGauge(ManualTemperatureGaugeType gauge) {
    this.manualTemperatureGauge = gauge;
    return this;
  }

  public InventoryItemDataBuilder withRemoteTemperatureMonitor(
      RemoteTemperatureMonitorType remoteTemperatureMonitor) {
    this.remoteTemperatureMonitor = remoteTemperatureMonitor;
    return this;
  }

  public InventoryItemDataBuilder withEquipmentTrackingId(String equipmentTrackingId) {
    this.equipmentTrackingId = equipmentTrackingId;
    return this;
  }

  public InventoryItemDataBuilder withReferenceName(String referenceName) {
    this.referenceName = referenceName;
    return this;
  }

  public InventoryItemDataBuilder withYearOfInstallation(Integer yearOfInstallation) {
    this.yearOfInstallation = yearOfInstallation;
    return this;
  }

  public InventoryItemDataBuilder withYearOfWarrantyExpiry(Integer withYearOfWarrantyExpiry) {
    this.yearOfWarrantyExpiry = withYearOfWarrantyExpiry;
    return this;
  }

  public InventoryItemDataBuilder withRemoteTemperatureMonitorId(String monitorId) {
    this.remoteTemperatureMonitorId = monitorId;
    return this;
  }

  public InventoryItemDataBuilder withAdditionalNotes(String additionalNotes) {
    this.additionalNotes = additionalNotes;
    return this;
  }

  public InventoryItemDataBuilder withDecommissionDate(LocalDate decommissionDate) {
    this.decommissionDate = decommissionDate;
    return this;
  }

  public InventoryItemDataBuilder withObsoleteStatus() {
    this.functionalStatus = FunctionalStatus.OBSOLETE;
    return this;
  }

  /**
   * Sets all invariant fields to different values than default.
   */
  public InventoryItemDataBuilder withDifferentInVariantFields() {
    catalogItem = new CatalogItemDataBuilder()
        .withoutFromPqsCatalogFlag()
        .withGasolineEnergySource()
        .withMinusThreeStorageTemperature()
        .withArchiveFlag()
        .build();

    programId = UUID.fromString("2465d0cd-df25-4492-94d7-8e08e9b36eb0");
    facilityId = UUID.fromString("8422e6fe-043b-4e4b-b01e-20df02996bfe");

    return this;
  }

  /**
   * Sets all variant fields to different values than default.
   */
  public InventoryItemDataBuilder withDifferentVariantFields() {
    functionalStatus = FunctionalStatus.NON_FUNCTIONING;
    reasonNotWorkingOrNotInUse = ReasonNotWorkingOrNotInUse.DEAD;
    utilization = Utilization.NOT_IN_USE;
    voltageStabilizer = VoltageStabilizerStatus.NO;
    backupGenerator = BackupGeneratorStatus.NO;
    voltageRegulator = VoltageRegulatorStatus.NO;
    manualTemperatureGauge = ManualTemperatureGaugeType.NO_GAUGE;
    remoteTemperatureMonitor = RemoteTemperatureMonitorType.NO_RTM;
    equipmentTrackingId = RandomStringGenerator.getInstance().generate(5);
    referenceName = RandomStringGenerator.getInstance().generate(5);
    yearOfInstallation = new Random().nextInt();
    yearOfWarrantyExpiry = new Random().nextInt();
    remoteTemperatureMonitorId = RandomStringGenerator.getInstance().generate(5);
    additionalNotes = RandomStringGenerator.getInstance().generate(5);
    decommissionDate = LocalDate.of(2010, 5, 5);
    lastModifierId = UUID.randomUUID();

    return this;
  }

  /**
   * Sets program and facility to random values.
   */
  public InventoryItemDataBuilder withRandomProgramAndFacility() {
    facilityId = UUID.randomUUID();
    programId = UUID.randomUUID();

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
