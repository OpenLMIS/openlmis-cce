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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.FacilityDto;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.service.referencedata.FacilityReferenceDataService;
import org.openlmis.cce.service.referencedata.UserReferenceDataService;

import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class InventoryItemDtoBuilderTest {

  @Mock
  private FacilityReferenceDataService facilityReferenceDataService;

  @Mock
  private UserReferenceDataService userReferenceDataService;

  @InjectMocks
  private InventoryItemDtoBuilder builder;

  @Mock
  private InventoryItem inventoryItem;

  @Mock
  private FacilityDto facility;

  @Mock
  private UserDto user;

  @Before
  public void setUp() throws Exception {
    when(facility.getId()).thenReturn(UUID.randomUUID());

    when(user.getId()).thenReturn(UUID.randomUUID());

    when(inventoryItem.getFacilityId()).thenReturn(UUID.randomUUID());
    when(inventoryItem.getLastModifierId()).thenReturn(UUID.randomUUID());
  }

  @Test
  public void shouldBuildDtoForList() throws Exception {
    builder.build(singletonList(inventoryItem));

    verify(facilityReferenceDataService).findOne(inventoryItem.getFacilityId());
    verify(userReferenceDataService).findOne(inventoryItem.getLastModifierId());
  }

  @Test
  public void shouldBuildDto() throws Exception {
    builder.build(inventoryItem);

    verify(facilityReferenceDataService).findOne(inventoryItem.getFacilityId());
    verify(userReferenceDataService).findOne(inventoryItem.getLastModifierId());
  }

}