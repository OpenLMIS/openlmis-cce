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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import java.util.Collections;
import java.util.UUID;

public class CatalogItemRepositoryIntegrationTest
    extends BaseCrudRepositoryIntegrationTest<CatalogItem> {

  @Autowired
  private CatalogItemRepository repository;

  private Pageable pageable = mock(Pageable.class);
  private CatalogItem catalogItem;

  @Override
  CrudRepository<CatalogItem, UUID> getRepository() {
    return repository;
  }

  @Override
  CatalogItem generateInstance() {
    return new CatalogItem(true, "equipment-code",
        "type", "model", "producent", EnergySource.ELECTRIC, 2016,
        StorageTemperature.MINUS3, 20, -20, "LOW", 1, 1, 1,
        new Dimensions(100, 100, 100), true, false);
  }

  @Before
  public void setUp() {
    catalogItem = repository.save(generateInstance());

    when(pageable.getPageSize()).thenReturn(10);
    when(pageable.getPageNumber()).thenReturn(0);

    CatalogItem itemArchived = new CatalogItem(true, "equipment-code2",
        "type2", "model2", "producent2", EnergySource.GASOLINE, 2017,
        StorageTemperature.PLUS1, 10, -10, "HIGH", 2, 2, 2,
        new Dimensions(10, 20, 30), false, true);
    repository.save(itemArchived);
  }

  @Test
  public void shouldFindCatalogItemBasedOnGivenParams() {
    Page<CatalogItem> foundItem =
        repository.findByArchivedAndTypeAndVisibleInCatalog(false, "type", true, pageable);
    assertEquals(Collections.singletonList(catalogItem), foundItem.getContent());
  }

  @Test
  public void shouldFindCatalogItemBasedOnArchivedAndVisibleInCatalog() {
    Page<CatalogItem> foundItem =
        repository.findByArchivedAndVisibleInCatalog(false, true, pageable);
    assertEquals(Collections.singletonList(catalogItem), foundItem.getContent());
  }
}
