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
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@TypeName("Inventory")
@Table(name = "cce_inventory_items", uniqueConstraints =
    @UniqueConstraint(name = "unq_inventory_catalog_eqid",
          columnNames = { "catalogitemid", "equipmenttrackingid" }))
@EqualsAndHashCode(callSuper = true)
public class InventoryItem extends BaseEntity {

  @Type(type = UUID)
  @Column(nullable = false)
  private UUID facilityId;

  @ManyToOne
  @Type(type = UUID)
  @JoinColumn(name = "catalogItemId", nullable = false)
  private CatalogItem catalogItem;

  @Type(type = UUID)
  @Column(nullable = false)
  private UUID programId;

  @Column(columnDefinition = TEXT)
  private String equipmentTrackingId;

  @Column(columnDefinition = TEXT, nullable = false)
  private String referenceName;

  @Column(nullable = false)
  private Integer yearOfInstallation;

  private Integer yearOfWarrantyExpiry;

  @Column(columnDefinition = TEXT)
  private String source;

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

  @Column(columnDefinition = "timestamp with time zone")
  private ZonedDateTime modifiedDate;

  @Type(type = UUID)
  private UUID lastModifierId;

  /**
   * Creates list of new instances based on data from {@link Importer} list
   *
   * @param importers {@link Importer} list
   * @return list of new Inventory instances.
   */
  public static List<InventoryItem> newInstance(Collection<? extends Importer> importers,
                                                UUID lastModifier) {
    return importers.stream()
        .map(i -> newInstance(i, lastModifier))
        .collect(Collectors.toList());
  }

  /**
   * Creates new instance based on data from {@link Importer}
   *
   * @param importer instance of {@link Importer}
   * @return new instance of Inventory.
   */
  public static InventoryItem newInstance(Importer importer, UUID lastModifierId) {
    InventoryItem inventoryItem = new InventoryItem();
    inventoryItem.id = importer.getId();
    inventoryItem.facilityId = importer.getFacilityId();
    inventoryItem.setCatalogItem(CatalogItem.newInstance(importer.getCatalogItem()));
    inventoryItem.programId = importer.getProgramId();
    inventoryItem.equipmentTrackingId = importer.getEquipmentTrackingId();
    inventoryItem.referenceName = importer.getReferenceName();
    inventoryItem.yearOfInstallation = importer.getYearOfInstallation();
    inventoryItem.yearOfWarrantyExpiry = importer.getYearOfWarrantyExpiry();
    inventoryItem.source = importer.getSource();
    inventoryItem.functionalStatus = importer.getFunctionalStatus();
    inventoryItem.reasonNotWorkingOrNotInUse = importer.getReasonNotWorkingOrNotInUse();
    inventoryItem.utilization = importer.getUtilization();
    inventoryItem.voltageStabilizer = importer.getVoltageStabilizer();
    inventoryItem.backupGenerator = importer.getBackupGenerator();
    inventoryItem.voltageRegulator = importer.getVoltageRegulator();
    inventoryItem.remoteTemperatureMonitor = importer.getRemoteTemperatureMonitor();
    inventoryItem.manualTemperatureGauge = importer.getManualTemperatureGauge();
    inventoryItem.remoteTemperatureMonitorId = importer.getRemoteTemperatureMonitorId();
    inventoryItem.decommissionDate = importer.getDecommissionDate();
    inventoryItem.additionalNotes = importer.getAdditionalNotes();
    inventoryItem.lastModifierId = lastModifierId;

    return inventoryItem;
  }

  /**
   * Set invariant fields based on other InventoryItem.
   *
   * @param item InventoryItem to set from
   */
  public void setInvariants(InventoryItem item) {
    if (item != null) {
      programId = item.getProgramId();
      facilityId = item.getFacilityId();
      catalogItem = item.getCatalogItem();
    }
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
  }
}
