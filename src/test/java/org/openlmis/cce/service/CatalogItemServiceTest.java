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

package org.openlmis.cce.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.openlmis.cce.util.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogItemServiceTest {

  @Mock
  private CatalogItemRepository catalogRepository;

  @InjectMocks
  private CatalogItemService catalogItemService;

  private List<CatalogItem> itemsList;
  private CatalogItem item = mock(CatalogItem.class);
  private CatalogItem item2 = mock(CatalogItem.class);
  private Pageable pageable = mock(Pageable.class);

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    itemsList = Arrays.asList(item, item2);

    when(pageable.getPageSize()).thenReturn(10);
    when(pageable.getPageNumber()).thenReturn(0);
  }

  @Test
  public void shouldReturnAllElementsIfNoSearchCriteriaProvided() {
    when(catalogRepository.findAll(pageable))
        .thenReturn(Pagination.getPage(itemsList, null, itemsList.size()));

    Page<CatalogItem> actual = catalogItemService.search(new HashMap<>(), pageable);
    assertEquals(itemsList, actual.getContent());
  }

  @Test
  public void shouldFindCatalogItemsForGivenQueryParam() {
    String type = "some-type";
    List<CatalogItem> foundCatalogItems = Collections.singletonList(item);
    when(catalogRepository
        .findByArchivedAndTypeAndVisibleInCatalog(
            false, type, true, pageable))
        .thenReturn(Pagination.getPage(foundCatalogItems, null, 1));

    Map<String, Object> searchParams = new HashMap<>();
    searchParams.put("archived", false);
    searchParams.put("visibleInCatalog", true);
    searchParams.put("type", type);

    Page<CatalogItem> actual = catalogItemService.search(searchParams, pageable);
    assertEquals(foundCatalogItems, actual.getContent());
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldThrowExceptionIfThereIsNoValidParameterProvidedForSearch() {
    Map<String, Object> searchParams = new HashMap<>();
    searchParams.put("some-param", "some-value");
    catalogItemService.search(searchParams, pageable);
  }
}