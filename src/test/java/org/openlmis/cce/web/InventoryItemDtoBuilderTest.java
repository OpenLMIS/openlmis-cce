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

package org.openlmis.cce.web;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.service.referencedata.UserReferenceDataService;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class InventoryItemDtoBuilderTest {

  private static final String SERVICE_URL = "localhost";

  @Mock
  private UserReferenceDataService userReferenceDataService;

  @InjectMocks
  private InventoryItemDtoBuilder builder;

  @Mock
  private UserDto user;

  private InventoryItem inventoryItem = new InventoryItem();

  @Before
  public void setUp() throws Exception {
    when(user.getId()).thenReturn(UUID.randomUUID());

    inventoryItem.setFacilityId(UUID.randomUUID());
    inventoryItem.setLastModifierId(UUID.randomUUID());

    when(userReferenceDataService.findAll()).thenReturn(singletonList(user));

    ReflectionTestUtils.setField(builder, "serviceUrl", SERVICE_URL);
  }

  @Test
  public void shouldBuildDtoForList() throws Exception {
    builder.build(singletonList(inventoryItem));

    verify(userReferenceDataService).findOne(inventoryItem.getLastModifierId());
  }

  @Test
  public void shouldBuildDto() throws Exception {
    InventoryItemDto build = builder.build(inventoryItem);

    assertEquals(
        SERVICE_URL + BaseController.API_PATH + InventoryItemController.RESOURCE_PATH,
        build.getFacility().getHref());
    verify(userReferenceDataService).findOne(inventoryItem.getLastModifierId());
  }
  
  @Test
  public void shouldNotUseUserFromListIfIdsMismatchMatch() throws Exception {
    builder.build(singletonList(inventoryItem));

    verify(userReferenceDataService).findOne(inventoryItem.getLastModifierId());
  }
 
  @Test
  public void shouldUseUserFromListIfIdsMatch() throws Exception {
    UUID userId = UUID.randomUUID();
    
    when(user.getId()).thenReturn(userId);
    inventoryItem.setLastModifierId(userId);

    builder.build(singletonList(inventoryItem));
    
    verify(userReferenceDataService, never()).findOne(any(UUID.class));
  }

}