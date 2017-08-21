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

package org.openlmis.cce.repository;

import org.junit.Test;
import org.openlmis.cce.domain.BackupGeneratorStatus;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.EnergySource;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.domain.ManualTemperatureGaugeType;
import org.openlmis.cce.domain.ReasonNotWorkingOrNotInUse;
import org.openlmis.cce.domain.RemoteTemperatureMonitorType;
import org.openlmis.cce.domain.StorageTemperature;
import org.openlmis.cce.domain.Utilization;
import org.openlmis.cce.domain.VoltageRegulatorStatus;
import org.openlmis.cce.domain.VoltageStabilizerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.UUID;

public class InventoryItemRepositoryIntegrationTest
    extends BaseCrudRepositoryIntegrationTest<InventoryItem> {

  @Autowired
  private InventoryItemRepository repository;

  @Autowired
  private CatalogItemRepository catalogItemRepository;

  @Autowired
  private EntityManager entityManager;

  @Override
  CrudRepository<InventoryItem, UUID> getRepository() {
    return repository;
  }

  @Override
  InventoryItem generateInstance() {
    return generateInstance(null);
  }

  InventoryItem generateInstance(CatalogItem catalogItemToUse) {
    CatalogItem catalogItem;
    if (catalogItemToUse == null) {
      catalogItem = new CatalogItem();
      catalogItem.setFromPqsCatalog(true);
      catalogItem.setType("type");
      catalogItem.setModel("model");
      catalogItem.setManufacturer("manufacturer");
      catalogItem.setEnergySource(EnergySource.ELECTRIC);
      catalogItem.setStorageTemperature(StorageTemperature.MINUS10);
      catalogItem.setArchived(false);
      catalogItem = catalogItemRepository.save(catalogItem);
    } else {
      catalogItem = catalogItemToUse;
    }

    return new InventoryItem(UUID.randomUUID(), catalogItem, UUID.randomUUID(),
        "eqTrackingId", "Some Reference Name",
        2010, 2020,  "some source", FunctionalStatus.FUNCTIONING, true,
        ReasonNotWorkingOrNotInUse.NOT_APPLICABLE, Utilization.ACTIVE,
        VoltageStabilizerStatus.UNKNOWN, BackupGeneratorStatus.YES, VoltageRegulatorStatus.NO,
        ManualTemperatureGaugeType.BUILD_IN, RemoteTemperatureMonitorType.BUILD_IN, "someMonitorId",
        "example notes", LocalDate.of(2017, 1, 1), null, UUID.randomUUID());
  }

  @Test(expected = PersistenceException.class)
  public void shouldNotAllowItemsWithSameEquipmentIdAndCatalogItem() {
    InventoryItem item = generateInstance();
    InventoryItem item2 = generateInstance(item.getCatalogItem());

    item.setEquipmentTrackingId("SAME_ID");
    item2.setEquipmentTrackingId("SAME_ID");

    repository.save(item);
    repository.save(item2);

    entityManager.flush();
  }
}
