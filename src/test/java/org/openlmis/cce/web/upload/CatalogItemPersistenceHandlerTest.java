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

package org.openlmis.cce.web.upload;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.repository.CatalogItemRepository;
import java.util.UUID;

public class CatalogItemPersistenceHandlerTest {

  private static final String EQCODE = "eqcode";

  @Mock
  private CatalogItemRepository catalogItemRepository;

  @InjectMocks
  private CatalogItemPersistenceHandler catalogItemPersistenceHandler;

  private CatalogItem catalogItem;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    catalogItem = new CatalogItem();
    catalogItem.setEquipmentCode(EQCODE);
  }

  @Test
  public void shouldSetIdIfExistingItemFound() {
    CatalogItem existingCatalogItem = new CatalogItem();
    existingCatalogItem.setId(UUID.randomUUID());

    when(catalogItemRepository.findByEquipmentCode(EQCODE)).thenReturn(existingCatalogItem);

    catalogItemPersistenceHandler.execute(catalogItem);

    catalogItem.setId(existingCatalogItem.getId());
    verify(catalogItemRepository).save(catalogItem);
  }

  @Test
  public void shouldNotSetIdIfExistingItemNotFound() {
    catalogItemPersistenceHandler.execute(catalogItem);

    verify(catalogItemRepository).save(catalogItem);
  }
}