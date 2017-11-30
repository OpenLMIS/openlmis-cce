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

import org.hibernate.annotations.Type;
import org.javers.core.metamodel.annotation.TypeName;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@TypeName("Inventory")
@Table(name = "cce_inventory_items", uniqueConstraints =
    @UniqueConstraint(name = "unq_inventory_catalog_eqid",
          columnNames = { "catalogitemid", "equipmenttrackingid" }))
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InventoryItem extends BaseEntity {

  @Getter
  @Type(type = UUID)
  @Column(nullable = false)
  private final UUID facilityId;

  @ManyToOne
  @Type(type = UUID)
  @JoinColumn(name = "catalogItemId", nullable = false)
  private final CatalogItem catalogItem;

  @Getter
  @Type(type = UUID)
  @Column(nullable = false)
  private final UUID programId;

  @Column(columnDefinition = TEXT)
  private String equipmentTrackingId;

  @Column(columnDefinition = TEXT, nullable = false)
  private String referenceName;

  @Column(nullable = false)
  private Integer yearOfInstallation;

  private Integer yearOfWarrantyExpiry;

  @Column(columnDefinition = TEXT)
  private String source;

  @Getter
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private FunctionalStatus functionalStatus;

  @Enumerated(EnumType.STRING)
  private ReasonNotWorkingOrNotInUse reasonNotWorkingOrNotInUse;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Utilization utilization;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private VoltageStabilizerStatus voltageStabilizer;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private BackupGeneratorStatus backupGenerator;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private VoltageRegulatorStatus voltageRegulator;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ManualTemperatureGaugeType manualTemperatureGauge;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private RemoteTemperatureMonitorType remoteTemperatureMonitor;

  @Column(columnDefinition = TEXT)
  private String remoteTemperatureMonitorId;

  @Column(columnDefinition = TEXT)
  private String additionalNotes;

  private LocalDate decommissionDate;

  @Setter
  @Column(columnDefinition = "timestamp with time zone")
  private ZonedDateTime modifiedDate;

  @Type(type = UUID)
  @Getter
  @Column(nullable = false)
  private UUID lastModifierId;

  /**
   * Default constructor needed by framework.
   */
  private InventoryItem() {
    facilityId = null;
    catalogItem = null;
    programId = null;
  }

  /**
   * Creates new instance based on data from {@link Importer}
   *
   * @param importer instance of {@link Importer}
   * @return new instance of Inventory.
   */
  public static InventoryItem newInstance(Importer importer, UUID lastModifierId) {
    InventoryItem inventoryItem = new InventoryItem(
        importer.getFacilityId(),
        CatalogItem.newInstance(importer.getCatalogItem()),
        importer.getProgramId(),
        importer.getEquipmentTrackingId(),
        importer.getReferenceName(),
        importer.getYearOfInstallation(),
        importer.getYearOfWarrantyExpiry(),
        importer.getSource(),
        importer.getFunctionalStatus(),
        importer.getReasonNotWorkingOrNotInUse(),
        importer.getUtilization(),
        importer.getVoltageStabilizer(),
        importer.getBackupGenerator(),
        importer.getVoltageRegulator(),
        importer.getManualTemperatureGauge(),
        importer.getRemoteTemperatureMonitor(),
        importer.getRemoteTemperatureMonitorId(),
        importer.getAdditionalNotes(),
        importer.getDecommissionDate(),
        null,
        lastModifierId);
    inventoryItem.id = importer.getId();

    return inventoryItem;
  }

  /**
   * Copy all values except invariants (id, program, facility, catalog item).
   */
  public void updateFrom(InventoryItem inventoryItem) {
    equipmentTrackingId = inventoryItem.equipmentTrackingId;
    referenceName = inventoryItem.referenceName;
    yearOfInstallation = inventoryItem.yearOfInstallation;
    yearOfWarrantyExpiry = inventoryItem.yearOfWarrantyExpiry;
    source = inventoryItem.source;
    functionalStatus = inventoryItem.functionalStatus;
    utilization = inventoryItem.utilization;
    voltageStabilizer = inventoryItem.voltageStabilizer;
    backupGenerator = inventoryItem.backupGenerator;
    voltageRegulator = inventoryItem.voltageRegulator;
    manualTemperatureGauge = inventoryItem.manualTemperatureGauge;
    remoteTemperatureMonitor = inventoryItem.remoteTemperatureMonitor;
    remoteTemperatureMonitorId = inventoryItem.remoteTemperatureMonitorId;
    additionalNotes = inventoryItem.additionalNotes;
    decommissionDate = inventoryItem.decommissionDate;
    lastModifierId = inventoryItem.lastModifierId;
  }

  /**
   * Indicates if status changed.
   *
   * @param oldInventory and old inventory item.
   * @return true if status has changed. False otherwise or if param is null.
   */
  public boolean statusChanged(InventoryItem oldInventory) {
    return oldInventory != null && oldInventory.functionalStatus != functionalStatus;
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setId(id);
    exporter.setFacilityId(facilityId);
    exporter.setCatalogItem(catalogItem);
    exporter.setProgramId(programId);
    exporter.setEquipmentTrackingId(equipmentTrackingId);
    exporter.setReferenceName(referenceName);
    exporter.setYearOfInstallation(yearOfInstallation);
    exporter.setYearOfWarrantyExpiry(yearOfWarrantyExpiry);
    exporter.setSource(source);
    exporter.setFunctionalStatus(functionalStatus);
    exporter.setReasonNotWorkingOrNotInUse(reasonNotWorkingOrNotInUse);
    exporter.setUtilization(utilization);
    exporter.setVoltageStabilizer(voltageStabilizer);
    exporter.setBackupGenerator(backupGenerator);
    exporter.setVoltageRegulator(voltageRegulator);
    exporter.setManualTemperatureGauge(manualTemperatureGauge);
    exporter.setRemoteTemperatureMonitorId(remoteTemperatureMonitorId);
    exporter.setRemoteTemperatureMonitor(remoteTemperatureMonitor);
    exporter.setAdditionalNotes(additionalNotes);
    exporter.setDecommissionDate(decommissionDate);
    exporter.setModifiedDate(modifiedDate);
    exporter.setLastModifierId(lastModifierId);
  }

  public interface Exporter {
    void setId(java.util.UUID id);

    void setFacilityId(UUID facilityId);

    void setCatalogItem(CatalogItem catalogItemId);

    void setProgramId(UUID programId);

    void setEquipmentTrackingId(String equipmentTrackingId);

    void setReferenceName(String referenceName);

    void setYearOfInstallation(Integer yearOfInstallation);

    void setYearOfWarrantyExpiry(Integer yearOfWarrantyExpiry);

    void setSource(String source);

    void setFunctionalStatus(FunctionalStatus functionalStatus);

    void setReasonNotWorkingOrNotInUse(ReasonNotWorkingOrNotInUse reasonNotWorkingOrNotInUse);

    void setUtilization(Utilization utilization);

    void setVoltageStabilizer(VoltageStabilizerStatus voltageStabilizer);

    void setBackupGenerator(BackupGeneratorStatus backupGenerator);

    void setVoltageRegulator(VoltageRegulatorStatus voltageRegulator);

    void setManualTemperatureGauge(ManualTemperatureGaugeType manualTemperatureGauge);

    void setRemoteTemperatureMonitorId(String remoteTemperatureMonitorId);

    void setRemoteTemperatureMonitor(RemoteTemperatureMonitorType remoteTemperatureMonitor);

    void setAdditionalNotes(String additionalNotes);

    void setDecommissionDate(LocalDate decommissionDate);

    void setModifiedDate(ZonedDateTime modifiedDate);

    void setLastModifierId(UUID lastModifierId);
  }

  public interface Importer {
    UUID getId();

    UUID getFacilityId();

    CatalogItem.Importer getCatalogItem();

    UUID getProgramId();

    String getEquipmentTrackingId();

    String getReferenceName();

    Integer getYearOfInstallation();

    Integer getYearOfWarrantyExpiry();

    String getSource();

    FunctionalStatus getFunctionalStatus();

    ReasonNotWorkingOrNotInUse getReasonNotWorkingOrNotInUse();

    Utilization getUtilization();

    VoltageStabilizerStatus getVoltageStabilizer();

    BackupGeneratorStatus getBackupGenerator();

    VoltageRegulatorStatus getVoltageRegulator();

    ManualTemperatureGaugeType getManualTemperatureGauge();

    RemoteTemperatureMonitorType getRemoteTemperatureMonitor();

    LocalDate getDecommissionDate();

    String getRemoteTemperatureMonitorId();

    String getAdditionalNotes();

    UUID getLastModifierId();
  }
}
