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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.InventoryItemDataBuilder;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.dto.ObjectReferenceDto;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class InventoryItemDtoBuilderTest {

  private static final String SERVICE_URL = "localhost";

  @InjectMocks
  private InventoryItemDtoBuilder builder;

  private InventoryItem inventoryItem = new InventoryItemDataBuilder().build();

  @Before
  public void setUp() throws Exception {
    ReflectionTestUtils.setField(builder, "serviceUrl", SERVICE_URL);
  }

  @Test
  public void shouldBuildDtoForList() throws Exception {
    List<InventoryItemDto> items = builder.build(singletonList(inventoryItem));

    assertEquals(1, items.size());
  }

  @Test
  public void shouldBuildDto() throws Exception {
    InventoryItemDto build = builder.build(inventoryItem);

    checkLastModifier(build.getLastModifier());
    checkFacility(build.getFacility());
    checkProgram(build.getProgram());
  }

  private void checkLastModifier(ObjectReferenceDto user) {
    assertEquals(inventoryItem.getLastModifierId(), user.getId());
    assertEquals(
        SERVICE_URL + BaseController.API_PATH + "/users/" + inventoryItem.getLastModifierId(),
        user.getHref());
  }

  private void checkFacility(ObjectReferenceDto facility) {
    assertEquals(inventoryItem.getFacilityId(), facility.getId());
    assertEquals(
        SERVICE_URL + BaseController.API_PATH + "/facilities/" + inventoryItem.getFacilityId(),
        facility.getHref());
  }

  private void checkProgram(ObjectReferenceDto program) {
    assertEquals(inventoryItem.getProgramId(), program.getId());
    assertEquals(
        SERVICE_URL + BaseController.API_PATH + "/programs/" + inventoryItem.getProgramId(),
        program.getHref());
  }
}