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

package org.openlmis.cce.web.csv.recordhandler;

import static java.util.Collections.singletonList;
import static org.assertj.core.util.Lists.emptyList;
import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.repository.CatalogItemRepository;

import java.util.List;
import java.util.UUID;


public class CatalogItemWriterTest {

  @Captor
  private ArgumentCaptor<Iterable<CatalogItem>> catalogItemsCaptor;

  private static final String EQCODE = "eqcode";
  private static final String SOME_MAKE = "someManufacturer";
  private static final String SOME_MODEL = "someModel";

  @Mock
  private CatalogItemRepository catalogItemRepository;

  @InjectMocks
  private CatalogItemWriter catalogItemWriter;

  private CatalogItem catalogItem;
  private CatalogItem existingCatalogItem;
  private List<CatalogItem> catalogItems;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    catalogItem = new CatalogItem();
    catalogItem.setEquipmentCode(EQCODE);
    catalogItem.setManufacturer(SOME_MAKE);
    catalogItem.setModel(SOME_MODEL);

    existingCatalogItem = new CatalogItem();
    existingCatalogItem.setId(UUID.randomUUID());
    existingCatalogItem.setEquipmentCode(EQCODE);
    existingCatalogItem.setManufacturer(SOME_MAKE);
    existingCatalogItem.setModel(SOME_MODEL);

    catalogItems = singletonList(catalogItem);
  }

  @Test
  public void shouldSetIdIfExistingItemFoundByCode() {
    when(catalogItemRepository.findExisting(anyListOf(CatalogItem.class)))
        .thenReturn(singletonList(existingCatalogItem));

    catalogItemWriter.write(catalogItems);

    verify(catalogItemRepository).save(catalogItemsCaptor.capture());
    assertEquals(
        existingCatalogItem.getId(),
        newArrayList(catalogItemsCaptor.getValue()).get(0).getId()
    );
  }

  @Test
  public void shouldSetIdIfExistingItemFoundByTypeAndModel() {
    catalogItem.setEquipmentCode(null);
    catalogItem.setManufacturer(SOME_MAKE);
    catalogItem.setModel(SOME_MODEL);

    when(catalogItemRepository.findExisting(anyListOf(CatalogItem.class)))
        .thenReturn(singletonList(existingCatalogItem));

    catalogItemWriter.write(catalogItems);

    verify(catalogItemRepository).save(catalogItemsCaptor.capture());
    assertEquals(
        existingCatalogItem.getId(),
        newArrayList(catalogItemsCaptor.getValue()).get(0).getId()
    );
  }

  @Test
  public void shouldNotSetIdIfExistingItemNotFound() {
    when(catalogItemRepository.findExisting(anyListOf(CatalogItem.class)))
        .thenReturn(emptyList());

    catalogItemWriter.write(catalogItems);

    verify(catalogItemRepository).save(catalogItems);
  }

}