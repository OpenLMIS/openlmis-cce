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

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openlmis.cce.CatalogItemDataBuilder;
import org.openlmis.cce.InventoryItemDataBuilder;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

public class InventoryItemRepositoryIntegrationTest
    extends BaseCrudRepositoryIntegrationTest<InventoryItem> {

  private static final String EQUIPMENT_TRACKING_ID = "equipmentTrackingId";
  private static final String CATALOG_ITEM = "catalogItem";

  @Autowired
  private InventoryItemRepository repository;

  @Autowired
  private CatalogItemRepository catalogItemRepository;

  @Autowired
  private EntityManager entityManager;

  @Mock
  private Pageable pageable;

  private CatalogItem catalogItem;

  @Override
  CrudRepository<InventoryItem, UUID> getRepository() {
    return repository;
  }

  @Override
  InventoryItem generateInstance() {
    return getInventoryItemDataBuilder().build();
  }

  @Before
  public void beforeEach() {
    catalogItem = new CatalogItemDataBuilder().withoutId().build();
    catalogItem = catalogItemRepository.save(catalogItem);

    when(pageable.getPageSize()).thenReturn(10);
    when(pageable.getPageNumber()).thenReturn(0);
  }

  @Test(expected = PersistenceException.class)
  public void shouldNotAllowItemsWithSameEquipmentIdAndCatalogItem() {
    InventoryItem item = getInventoryItemDataBuilder()
        .withEquipmentTrackingId(EQUIPMENT_TRACKING_ID)
        .build();
    InventoryItem item2 = getInventoryItemDataBuilder()
        .withEquipmentTrackingId(EQUIPMENT_TRACKING_ID)
        .build();

    repository.save(item);
    repository.save(item2);

    entityManager.flush();
  }

  @Test
  public void shouldFindInventoryItemsByFacilityIds() {
    InventoryItem item = generateInstance();
    item = repository.save(item);

    InventoryItem item2 = getInventoryItemDataBuilder()
        .withFacilityId(item.getFacilityId())
        .build();
    repository.save(item2);

    InventoryItem item3 = generateInstance();
    item3 = repository.save(item3);

    InventoryItem item4 = generateInstance();
    repository.save(item4);

    Page<InventoryItem> inventoryItems = repository.search(Arrays.asList(item.getFacilityId(),
        item3.getFacilityId()), null, null, pageable);

    assertEquals(3, inventoryItems.getTotalElements());
    for (InventoryItem inventoryItem : inventoryItems) {
      assertTrue(inventoryItem.getFacilityId().equals(item.getFacilityId())
          || inventoryItem.getFacilityId().equals(item3.getFacilityId()));
    }

    inventoryItems = repository.search(singletonList(item.getFacilityId()), null, null, pageable);

    assertEquals(2, inventoryItems.getTotalElements());
    for (InventoryItem inventoryItem : inventoryItems) {
      assertTrue(inventoryItem.getFacilityId().equals(item.getFacilityId()));
    }
  }

  @Test
  public void shouldFindInventoryItemsByProgramIds() {
    InventoryItem item = generateInstance();
    item = repository.save(item);

    InventoryItem item2 = getInventoryItemDataBuilder()
        .withProgramId(item.getProgramId())
        .build();
    repository.save(item2);

    InventoryItem item3 = generateInstance();
    item3 = repository.save(item3);

    InventoryItem item4 = generateInstance();
    repository.save(item4);

    Page<InventoryItem> inventoryItems = repository.search(null, Arrays.asList(item.getProgramId(),
        item3.getProgramId()), null, pageable);

    assertEquals(3, inventoryItems.getTotalElements());
    for (InventoryItem inventoryItem : inventoryItems) {
      assertTrue(inventoryItem.getProgramId().equals(item.getProgramId())
          || inventoryItem.getProgramId().equals(item3.getProgramId()));
    }

    inventoryItems = repository.search(null, singletonList(item.getProgramId()), null, pageable);

    assertEquals(2, inventoryItems.getTotalElements());
    for (InventoryItem inventoryItem : inventoryItems) {
      assertTrue(inventoryItem.getProgramId().equals(item.getProgramId()));
    }
  }

  @Test
  public void shouldFindInventoryItemsByFacilityIdsAndProgramIds() {
    InventoryItem item = generateInstance();
    item = repository.save(item);

    InventoryItem item2 = generateInstance();
    item2 = repository.save(item2);

    InventoryItem item3 = getInventoryItemDataBuilder()
        .withFacilityId(item.getFacilityId())
        .withProgramId(item2.getProgramId())
        .build();

    item3 = repository.save(item3);

    Page<InventoryItem> inventoryItems = repository.search(singletonList(item.getFacilityId()),
        singletonList(item2.getProgramId()), null, pageable);

    assertEquals(1, inventoryItems.getTotalElements());
    assertTrue(inventoryItems.getContent().get(0).getProgramId().equals(item3.getProgramId()));
  }

  @Test
  public void shouldFindInventoryItemsByFunctionalStatus() {
    InventoryItem item1 = getInventoryItemDataBuilder().build();
    InventoryItem item2 = getInventoryItemDataBuilder().build();

    InventoryItem item3 = getInventoryItemDataBuilder()
        .withStatus(FunctionalStatus.OBSOLETE)
        .build();

    InventoryItem item4 = getInventoryItemDataBuilder()
        .withStatus(FunctionalStatus.OBSOLETE)
        .build();

    repository.save(item1);
    repository.save(item2);
    repository.save(item3);
    repository.save(item4);

    Page<InventoryItem> inventoryItems = repository.search(
        null,
        null,
        FunctionalStatus.FUNCTIONING,
        pageable
    );

    assertEquals(2, inventoryItems.getTotalElements());
    assertTrue(inventoryItems.getContent().get(0).getFunctionalStatus()
        .equals(FunctionalStatus.FUNCTIONING));
    assertTrue(inventoryItems.getContent().get(1).getFunctionalStatus()
        .equals(FunctionalStatus.FUNCTIONING));
  }

  @Test
  public void shouldSortInventoryItems() {
    when(pageable.getSort()).thenReturn(new Sort("type", EQUIPMENT_TRACKING_ID));
    InventoryItem item = generateInstance();
    repository.save(item);

    CatalogItem catalogItem = new CatalogItemDataBuilder()
        .withoutId()
        .withType("otherType")
        .withModel("new-model")
        .withManufacturer("some-manufacturer")
        .build();
    catalogItem = catalogItemRepository.save(catalogItem);

    InventoryItem item2 = getInventoryItemDataBuilder()
        .withCatalogItem(catalogItem)
        .build();
    repository.save(item2);

    InventoryItem item3 = getInventoryItemDataBuilder()
        .withCatalogItem(catalogItem)
        .build();
    repository.save(item3);

    Page<InventoryItem> inventoryItems = repository.search(null, null, null, pageable);

    assertEquals(3, inventoryItems.getTotalElements());
    InventoryItem inventoryItem0 = inventoryItems.getContent().get(0);
    InventoryItem inventoryItem1 = inventoryItems.getContent().get(1);
    InventoryItem inventoryItem2 = inventoryItems.getContent().get(2);
    String equipmentTrackingId0 =
        (String) ReflectionTestUtils.getField(inventoryItem0, EQUIPMENT_TRACKING_ID);
    String equipmentTrackingId1 =
        (String) ReflectionTestUtils.getField(inventoryItem1, EQUIPMENT_TRACKING_ID);

    assertTrue(equipmentTrackingId0.compareTo(equipmentTrackingId1) < 0);

    CatalogItem catalogItem1 =
        (CatalogItem) ReflectionTestUtils.getField(inventoryItem1, CATALOG_ITEM);
    CatalogItem catalogItem2 =
        (CatalogItem) ReflectionTestUtils.getField(inventoryItem2, CATALOG_ITEM);

    assertTrue(catalogItem1.getType().compareTo(catalogItem2.getType()) < 0);
  }

  private InventoryItemDataBuilder getInventoryItemDataBuilder() {
    return new InventoryItemDataBuilder()
        .withId(null)
        .withCatalogItem(catalogItem)
        .withEquipmentTrackingId("eqTrackingId" + getNextInstanceNumber());
  }
}
