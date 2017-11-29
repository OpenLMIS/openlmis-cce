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
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openlmis.cce.domain.CatalogItem.EQUIPMENT_CODE;
import static org.openlmis.cce.domain.CatalogItem.MANUFACTURER_FIELD;
import static org.openlmis.cce.domain.CatalogItem.MODEL_FIELD;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.Dimensions;
import org.openlmis.cce.domain.EnergySource;
import org.openlmis.cce.domain.StorageTemperature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

@SuppressWarnings({"PMD.TooManyMethods"})
public class CatalogItemRepositoryIntegrationTest
    extends BaseCrudRepositoryIntegrationTest<CatalogItem> {

  @Autowired
  private CatalogItemRepository repository;

  @Autowired
  private EntityManager entityManager;

  private Pageable pageable = mock(Pageable.class);

  @Override
  CrudRepository<CatalogItem, UUID> getRepository() {
    return repository;
  }

  @Override
  CatalogItem generateInstance() {
    return new CatalogItem(true, "equipment-code" + getNextInstanceNumber(),
        "type", "model", "producent" + getNextInstanceNumber(), EnergySource.ELECTRIC, 2016,
        StorageTemperature.MINUS3, 20, -20, "LOW", 1, 1, 1,
        new Dimensions(100, 100, 100), true, false);
  }

  @Before
  public void setUp() {
    repository.save(generateInstance());

    when(pageable.getPageSize()).thenReturn(10);
    when(pageable.getPageNumber()).thenReturn(0);

    CatalogItem itemArchived = new CatalogItem(true, "equipment-code2",
        "type2", "model2", "producent2", EnergySource.GASOLINE, 2017,
        StorageTemperature.PLUS1, 10, -10, "HIGH", 2, 2, 2,
        new Dimensions(10, 20, 30), false, true);
    repository.save(itemArchived);
  }

  @Test(expected = PersistenceException.class)
  public void shouldNotAllowToCreateCatalogItemsWithSameEquipmentCode() {
    CatalogItem item1 = generateInstance();
    repository.save(item1);

    CatalogItem item2 = generateInstance();
    item2.setEquipmentCode(item1.getEquipmentCode());
    repository.save(item2);

    entityManager.flush();
  }

  @Test(expected = PersistenceException.class)
  public void shouldNotAllowToCreateCatalogItemsWithSameMakeModel() {
    CatalogItem item1 = generateInstance();
    repository.save(item1);

    CatalogItem item2 = generateInstance();
    item2.setModel(item1.getModel());
    item2.setManufacturer(item1.getManufacturer());
    repository.save(item2);

    entityManager.flush();
  }

  @Test
  public void shouldCheckIfCatalogItemWithSpecifiedEquipmentCodeExists() {
    CatalogItem item = generateInstance();
    repository.save(item);

    assertTrue(repository.existsByEquipmentCode(item.getEquipmentCode()));
    assertFalse(repository.existsByEquipmentCode(item.getEquipmentCode() + "a"));
  }

  @Test
  public void shouldCheckIfCatalogItemWithManufacturerAndModelExists() {
    CatalogItem item = generateInstance();
    repository.save(item);

    assertTrue(repository.existsByManufacturerAndModel(item.getManufacturer(), item.getModel()));
    assertFalse(repository.existsByManufacturerAndModel(item.getManufacturer() + "a",
        item.getModel()));
    assertFalse(repository.existsByManufacturerAndModel(item.getManufacturer(),
        item.getModel() + "a"));
  }

  @Test
  public void shouldReturnAllCatalogItemsWhenSearchingWithNullValues() {
    CatalogItem item = generateInstance();
    repository.save(item);

    Page<CatalogItem> items = repository.search(null, null, null, pageable);

    assertEquals(3, items.getContent().size());
  }

  @Test
  public void shouldSearchCatalogItemsByType() {
    CatalogItem item = generateInstance();
    item.setType("some-type");
    repository.save(item);

    Page<CatalogItem> items = repository.search("some-type", null, null, pageable);

    assertEquals(1, items.getContent().size());
    assertEquals("some-type", items.getContent().get(0).getType());
  }

  @Test
  public void shouldSearchCatalogItemsByArchived() {
    CatalogItem item = generateInstance();
    repository.save(item);

    Page<CatalogItem> items = repository.search(null, true, null, pageable);

    assertEquals(1, items.getContent().size());
    assertEquals(true, items.getContent().get(0).getArchived());
  }

  @Test
  public void shouldSearchCatalogItemsByVisibleInCatalog() {
    CatalogItem item = generateInstance();
    item.setVisibleInCatalog(false);
    repository.save(item);

    Page<CatalogItem> items = repository.search(null, null, false, pageable);

    assertEquals(2, items.getContent().size());
    assertEquals(false, items.getContent().get(0).getVisibleInCatalog());
  }

  @Test
  public void shouldSearchCatalogItemsByAllParameters() {
    CatalogItem item = generateInstance();
    item.setType("other-type");
    item.setArchived(false);
    item.setVisibleInCatalog(false);
    repository.save(item);

    Page<CatalogItem> items = repository.search("other-type", false, false, pageable);

    assertEquals(1, items.getContent().size());
    assertEquals("other-type", items.getContent().get(0).getType());
    assertEquals(false, items.getContent().get(0).getArchived());
    assertEquals(false, items.getContent().get(0).getVisibleInCatalog());
  }

  @Test
  public void shouldFindExistingByEquipmentCode() throws Exception {
    CatalogItem item = generateInstance();
    repository.save(item);

    List<CatalogItem> found = repository.findExisting(singletonList(item));

    assertThat(found, hasSize(1));
    assertThat(found.get(0), hasProperty(EQUIPMENT_CODE, equalTo(item.getEquipmentCode())));
  }

  @Test
  public void shouldFindExistingByManufacturerAndModel() throws Exception {
    CatalogItem item = generateInstance();
    item.setEquipmentCode(null);

    repository.save(item);

    List<CatalogItem> found = repository.findExisting(singletonList(item));

    assertThat(found, hasSize(1));
    assertThat(found.get(0), allOf(
        hasProperty(MANUFACTURER_FIELD, equalTo(item.getManufacturer())),
        hasProperty(MODEL_FIELD, equalTo(item.getModel()))
    ));
  }
}
