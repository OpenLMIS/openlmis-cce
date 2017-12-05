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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_EDIT;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_VIEW;

import org.javers.common.collections.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.cce.InventoryItemDataBuilder;
import org.openlmis.cce.InventoryItemSearchParamsDataBuilder;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.PermissionStringDto;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.openlmis.cce.util.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InventoryItemServiceTest {

  @Mock
  private InventoryItemRepository repository;

  @Mock
  private PermissionService permissionService;

  @Mock
  private Pageable pageable;

  @InjectMocks
  private InventoryItemService service;

  private UUID userId;
  private InventoryItem inventoryItem;
  private InventoryItemSearchParams params;
  private Set<PermissionStringDto> permissionStrings;
  private Page<InventoryItem> expectedPage;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    permissionStrings = new HashSet<>();

    params = new InventoryItemSearchParamsDataBuilder().build();

    userId = UUID.randomUUID();
    PermissionStrings.Handler handler = mock(PermissionStrings.Handler.class);
    when(handler.get()).thenReturn(permissionStrings);
    when(permissionService.getPermissionStrings(userId)).thenReturn(handler);

    inventoryItem = new InventoryItemDataBuilder().build();
    expectedPage = Pagination.getPage(Collections.singletonList(inventoryItem), null, 1);
  }

  @Test
  public void searchShouldReturnPageForValidParams() {
    UUID facilityId = UUID.randomUUID();

    UUID programId1 = UUID.randomUUID();
    addPermission(CCE_INVENTORY_VIEW, facilityId, programId1);

    UUID programId2 = UUID.randomUUID();
    addPermission(CCE_INVENTORY_VIEW, facilityId, programId2);

    InventoryItemSearchParams params = new InventoryItemSearchParamsDataBuilder()
        .withFacilityId(facilityId)
        .build();

    when(repository.search(
        eq(Collections.singleton(facilityId)),
        eq(Sets.asSet(programId1, programId2)),
        eq(params.getFunctionalStatus()),
        eq(pageable)
    )).thenReturn(expectedPage);

    Page<InventoryItem> page = service.search(userId, params, pageable);

    assertEquals(expectedPage, page);
  }

  @Test
  public void searchShouldReturnPageForEmptySearchParams() {
    UUID programId1 = UUID.randomUUID();
    UUID facilityId1 = UUID.randomUUID();
    addPermission(CCE_INVENTORY_VIEW, facilityId1, programId1);

    UUID programId2 = UUID.randomUUID();
    UUID facilityId2 = UUID.randomUUID();
    addPermission(CCE_INVENTORY_VIEW, facilityId2, programId2);

    InventoryItemSearchParams params = InventoryItemSearchParamsDataBuilder.buildEmpty();

    when(repository.search(
        eq(Sets.asSet(facilityId1, facilityId2)),
        eq(Sets.asSet(programId1, programId2)),
        eq(null),
        eq(pageable)
    )).thenReturn(expectedPage);

    Page<InventoryItem> page = service.search(userId, params, pageable);

    assertEquals(expectedPage, page);
  }

  @Test
  public void searchShouldIgnoreNonCceInventoryViewRights() {
    UUID facilityId = UUID.randomUUID();

    UUID programId1 = UUID.randomUUID();
    addPermission(CCE_INVENTORY_VIEW, facilityId, programId1);

    UUID programId2 = UUID.randomUUID();
    addPermission(CCE_INVENTORY_EDIT, facilityId, programId2);

    InventoryItemSearchParams params = new InventoryItemSearchParamsDataBuilder()
        .withFacilityId(facilityId)
        .build();

    when(repository.search(
        eq(Collections.singleton(facilityId)),
        eq(Sets.asSet(programId1)),
        eq(params.getFunctionalStatus()),
        eq(pageable)
    )).thenReturn(expectedPage);

    Page<InventoryItem> page = service.search(userId, params, pageable);

    assertEquals(expectedPage, page);
  }

  @Test(expected = Exception.class)
  public void searchShouldThrowExceptionForMissingPageable() {
    when(repository.search(
        any(),
        any(),
        any(),
        eq(null)
    )).thenThrow(new Exception());

    service.search(userId, params, null);
  }

  @Test(expected = Exception.class)
  public void shouldThrowExceptionForMissingSearchParams() {
    service.search(userId, null, pageable);
  }

  @Test(expected = Exception.class)
  public void shouldThrowExceptionForMissingUserId() {
    service.search(null, params, pageable);
  }

  private void addPermission(String name, UUID facilityId, UUID programId) {
    PermissionStringDto permission = PermissionStringDto.create(
        name, facilityId, programId
    );
    permissionStrings.add(permission);
  }

}