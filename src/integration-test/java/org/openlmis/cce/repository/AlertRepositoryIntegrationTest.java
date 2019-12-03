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

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.AlertDataBuilder;
import org.openlmis.cce.CatalogItemDataBuilder;
import org.openlmis.cce.InventoryItemDataBuilder;
import org.openlmis.cce.domain.Alert;
import org.openlmis.cce.domain.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;

public class AlertRepositoryIntegrationTest
    extends BaseCrudRepositoryIntegrationTest<Alert> {

  private static final String STATUS_LOCALE = "en-US";
  private static final String ALERT_TYPE_NOT_WORKING_FREEZING = "not_working_freezing";
  private static final String ALERT_TYPE_NO_DATA = "no_data";
  private static final String STATUS_MESSAGE_EQUIPMENT_TOO_HOT =
      "Equipment needs attention: too hot";
  private static final String STATUS_MESSAGE_EQUIPMENT_FREEZING =
      "Equipment needs attention: freezing";
  private static final String STATUS_MESSAGE_NOT_ENOUGH_DATA =
      "Not enough data from equipment";

  @Autowired
  private AlertRepository repository;

  @Autowired
  private InventoryItemRepository inventoryItemRepository;

  @Autowired
  private CatalogItemRepository catalogItemRepository;
  
  private PageRequest pageRequest = PageRequest.of(0, 10);

  private InventoryItem inventoryItem1;
  private Alert activeAlert1;
  private String activeAlert1Id;
  private Alert inactiveAlert1;
  private InventoryItem inventoryItem2;
  private Alert inactiveAlert2;
  private InventoryItem inventoryItem3;
  private Alert inactiveAlert3;
  private Alert inactiveAlert3b;
  private Alert activeAlert3;

  @Override
  CrudRepository<Alert, UUID> getRepository() {
    return repository;
  }

  @Override
  Alert generateInstance() {
    activeAlert1Id = UUID.randomUUID().toString();
    return new AlertDataBuilder()
        .withExternalId(activeAlert1Id)
        .withInventoryItem(inventoryItem1)
        .withoutEndTimestamp()
        .withStatusMessages(Collections.singletonMap(
            STATUS_LOCALE, STATUS_MESSAGE_EQUIPMENT_TOO_HOT))
        .withoutDismissTimestamp()
        .buildAsNew();
  }

  @Before
  public void setUp() {
    catalogItemRepository.save(new CatalogItemDataBuilder().build());
    inventoryItem1 = inventoryItemRepository.save(new InventoryItemDataBuilder().build());

    activeAlert1 = repository.save(generateInstance());
    inactiveAlert1 = new AlertDataBuilder()
        .withType(ALERT_TYPE_NOT_WORKING_FREEZING)
        .withInventoryItem(inventoryItem1)
        .withStatusMessages(Collections.singletonMap(
            STATUS_LOCALE, STATUS_MESSAGE_EQUIPMENT_FREEZING))
        .buildAsNew();
    repository.save(inactiveAlert1);

    inventoryItem2 = inventoryItemRepository.save(new InventoryItemDataBuilder()
        .withId(UUID.randomUUID())
        .withEquipmentTrackingId("another-tracking-id")
        .build());

    inactiveAlert2 = new AlertDataBuilder()
        .withType(ALERT_TYPE_NOT_WORKING_FREEZING)
        .withInventoryItem(inventoryItem2)
        .withoutEndTimestamp()
        .withStatusMessages(Collections.singletonMap(
            STATUS_LOCALE, STATUS_MESSAGE_EQUIPMENT_FREEZING))
        .buildAsNew();
    repository.save(inactiveAlert2);

    inventoryItem3 = inventoryItemRepository.save(new InventoryItemDataBuilder()
        .withId(UUID.randomUUID())
        .withEquipmentTrackingId("third-tracking-id")
        .build());

    activeAlert3 = new AlertDataBuilder()
        .withType(ALERT_TYPE_NO_DATA)
        .withInventoryItem(inventoryItem3)
        .withoutEndTimestamp()
        .withStatusMessages(Collections.singletonMap(
            STATUS_LOCALE, STATUS_MESSAGE_NOT_ENOUGH_DATA))
        .withoutDismissTimestamp()
        .buildAsNew();
    repository.save(activeAlert3);

    inactiveAlert3 = new AlertDataBuilder()
        .withType(ALERT_TYPE_NO_DATA)
        .withInventoryItem(inventoryItem3)
        .withStatusMessages(Collections.singletonMap(
            STATUS_LOCALE, STATUS_MESSAGE_NOT_ENOUGH_DATA))
        .withoutDismissTimestamp()
        .buildAsNew();
    repository.save(inactiveAlert3);

    inactiveAlert3b = new AlertDataBuilder()
        .withType(ALERT_TYPE_NO_DATA)
        .withInventoryItem(inventoryItem3)
        .withStatusMessages(Collections.singletonMap(
            STATUS_LOCALE, STATUS_MESSAGE_NOT_ENOUGH_DATA))
        .withoutDismissTimestamp()
        .buildAsNew();
    repository.save(inactiveAlert3b);
  }

  @Test
  public void findByActiveAndInventoryItemIdsInShouldFindActiveMatchingAlertsWhenActiveIsTrue() {

    //when
    Page<Alert> alertsPage = repository.findByActiveAndInventoryItemIdIn(true,
        Arrays.asList(inventoryItem1.getId(), inventoryItem2.getId()), pageRequest);

    //then
    assertEquals(1, alertsPage.getTotalElements());
    assertTrue(alertsPage.getContent().contains(activeAlert1));
  }

  @Test
  public void findByActiveAndInventoryItemIdsInShouldFindInactiveMatchingAlertsWhenActiveIsFalse() {

    //when
    Page<Alert> alertsPage = repository.findByActiveAndInventoryItemIdIn(false,
        Arrays.asList(inventoryItem1.getId(), inventoryItem2.getId()), pageRequest);

    //then
    assertEquals(2, alertsPage.getTotalElements());
    assertTrue(alertsPage.getContent().contains(inactiveAlert1));
    assertTrue(alertsPage.getContent().contains(inactiveAlert2));
  }

  @Test
  public void findByActiveShouldFindActiveAlertsWhenActiveIsTrue() {

    //when
    Page<Alert> alerts = repository.findByActive(true, pageRequest);

    //then
    assertEquals(2, alerts.getTotalElements());
    assertTrue(alerts.getContent().contains(activeAlert1));
    assertTrue(alerts.getContent().contains(activeAlert3));
  }

  @Test
  public void findByActiveShouldFindInactiveAlertsWhenActiveIsFalse() {

    //when
    Page<Alert> alerts = repository.findByActive(false, pageRequest);

    //then
    assertEquals(4, alerts.getTotalElements());
    assertTrue(alerts.getContent().contains(inactiveAlert1));
    assertTrue(alerts.getContent().contains(inactiveAlert2));
    assertTrue(alerts.getContent().contains(inactiveAlert3));
    assertTrue(alerts.getContent().contains(inactiveAlert3b));
  }

  @Test
  public void findByInventoryItemIdInShouldFindMatchingAlerts() {

    //when
    Page<Alert> alerts = repository.findByInventoryItemIdIn(
        Arrays.asList(inventoryItem1.getId(), inventoryItem2.getId()), pageRequest);

    //then
    assertEquals(3, alerts.getTotalElements());
    assertTrue(alerts.getContent().contains(activeAlert1));
    assertTrue(alerts.getContent().contains(inactiveAlert1));
    assertTrue(alerts.getContent().contains(inactiveAlert2));
  }

  @Test
  public void existsByExternalIdShouldReturnTrueIfExists() {

    //when
    boolean exists = repository.existsByExternalId(activeAlert1Id);

    //then
    assertTrue(exists);
  }

  @Test
  public void existsByExternalIdShouldReturnFalseIfDoesNotExist() {

    //when
    boolean exists = repository.existsByExternalId(UUID.randomUUID().toString());

    //then
    assertFalse(exists);
  }

  @Test
  public void findByExternalIdShouldReturnMatchingAlert() {

    //when
    Alert alert = repository.findByExternalId(activeAlert1Id);

    //then
    assertEquals(activeAlert1, alert);
  }
}
