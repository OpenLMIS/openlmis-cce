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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.cce.CatalogItemDataBuilder;
import org.openlmis.cce.InventoryItemDataBuilder;
import org.openlmis.cce.domain.Alert;
import org.openlmis.cce.domain.AlertType;
import org.openlmis.cce.domain.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public class AlertRepositoryIntegrationTest
    extends BaseCrudRepositoryIntegrationTest<Alert> {

  private static final String STATUS_LOCALE = "en_US";

  @Autowired
  private AlertRepository repository;

  @Autowired
  private InventoryItemRepository inventoryItemRepository;

  @Autowired
  private CatalogItemRepository catalogItemRepository;
  
  @Mock
  private Pageable pageable;

  private InventoryItem inventoryItem;
  private InventoryItem inventoryItem2;

  @Override
  CrudRepository<Alert, UUID> getRepository() {
    return repository;
  }

  @Override
  Alert generateInstance() {
    return Alert.createNew(AlertType.not_working_hot,
        inventoryItem,
        ZonedDateTime.now(),
        null,
        Collections.singletonMap(STATUS_LOCALE, "Equipment needs attention: too hot"),
        null);
  }

  @Before
  public void setUp() {
    catalogItemRepository.save(new CatalogItemDataBuilder().build());
    
    inventoryItem = new InventoryItemDataBuilder().build();
    inventoryItem = inventoryItemRepository.save(inventoryItem);

    inventoryItem2 = new InventoryItemDataBuilder()
        .withId(UUID.randomUUID())
        .withEquipmentTrackingId("another-tracking-id")
        .build();
    inventoryItem2 = inventoryItemRepository.save(inventoryItem2);

    when(pageable.getPageSize()).thenReturn(10);
    when(pageable.getPageNumber()).thenReturn(0);
  }

  @Test
  public void findByInventoryItemIdInShouldFindAllMatchingAlertsForOneId() {
    Alert item = generateInstance();
    Alert item2 = Alert.createNew(AlertType.not_working_freezing,
        inventoryItem2,
        ZonedDateTime.now(),
        null,
        Collections.singletonMap(STATUS_LOCALE, "Equipment needs attention: freezing"),
        null);
    repository.save(item);
    repository.save(item2);
    
    Page<Alert> alertsPage = repository.findByInventoryItemIdIn(
        Collections.singletonList(inventoryItem.getId()), pageable);

    assertEquals(1, alertsPage.getTotalElements());
    Alert firstAlert = alertsPage.getContent().get(0);
    assertEquals(AlertType.not_working_hot, firstAlert.getType());
    assertEquals(inventoryItem, firstAlert.getInventoryItem());
    assertEquals("Equipment needs attention: too hot", 
        firstAlert.getStatusMessages().get(STATUS_LOCALE));
  }

  @Test
  public void findByInventoryItemIdInShouldFindAllMatchingAlertsForMultipleIds() {
    InventoryItem inventoryItem3 = new InventoryItemDataBuilder()
        .withId(UUID.randomUUID())
        .withEquipmentTrackingId("third-tracking-id")
        .build();
    inventoryItem3 = inventoryItemRepository.save(inventoryItem3);

    Alert item = generateInstance();
    Alert item2 = Alert.createNew(AlertType.not_working_freezing,
        inventoryItem2,
        ZonedDateTime.now(),
        null,
        Collections.singletonMap(STATUS_LOCALE, "Equipment needs attention: freezing"),
        null);
    Alert item3 = Alert.createNew(AlertType.no_data,
        inventoryItem3,
        ZonedDateTime.now(),
        null,
        Collections.singletonMap(STATUS_LOCALE, "Not enough data from equipment"),
        null);
    item = repository.save(item);
    item2 = repository.save(item2);
    item3 = repository.save(item3);

    Page<Alert> alertsPage = repository.findByInventoryItemIdIn(
        Arrays.asList(inventoryItem.getId(), inventoryItem2.getId()), pageable);

    assertEquals(2, alertsPage.getTotalElements());
    assertTrue(alertsPage.getContent().contains(item));
    assertTrue(alertsPage.getContent().contains(item2));
    assertFalse(alertsPage.getContent().contains(item3));
  }
}
