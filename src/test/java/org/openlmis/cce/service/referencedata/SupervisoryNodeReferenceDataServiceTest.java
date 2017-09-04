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

package org.openlmis.cce.service.referencedata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.dto.SupervisoryNodeDto;
import org.openlmis.cce.service.RequestParameters;
import org.openlmis.cce.testutil.DummyPage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SupervisoryNodeReferenceDataServiceTest {

  private static final String FACILITY_ID = "facilityId";
  private static final String SEARCH_URI = "search";
  private UUID facility = UUID.randomUUID();
  private SupervisoryNodeDto supervisoryNode = mock(SupervisoryNodeDto.class);
  private SupervisoryNodeReferenceDataService spy;

  @Before
  public void setUp() {
    spy = spy(new SupervisoryNodeReferenceDataService());
  }

  @Test
  public void shouldReturnNullIfEmptyPage() {
    doReturn(new DummyPage<SupervisoryNodeDto>(Collections.emptyList()))
        .when(spy)
        .getPage(
            eq(SEARCH_URI),
            any(),
            refEq(RequestParameters.init().set(FACILITY_ID, facility)));

    assertNull(spy.findSupervisoryNode(facility));
  }

  @Test
  public void shouldReturnFirstElementIfOneFound() {
    SupervisoryNodeReferenceDataService spy = spy(new SupervisoryNodeReferenceDataService());
    doReturn(new DummyPage<>(Collections.singletonList(supervisoryNode)))
        .when(spy)
        .getPage(
            eq(SEARCH_URI),
            any(),
            refEq(RequestParameters.init().set(FACILITY_ID, facility)));

    SupervisoryNodeDto foundNode = spy.findSupervisoryNode(facility);

    assertEquals(supervisoryNode, foundNode);
  }

  @Test
  public void shouldReturnFirstElementIfMoreThanOneFound() {
    SupervisoryNodeReferenceDataService spy = spy(new SupervisoryNodeReferenceDataService());
    SupervisoryNodeDto secondNode = new SupervisoryNodeDto();
    List<SupervisoryNodeDto> found = Arrays.asList(supervisoryNode, secondNode);
    doReturn(new DummyPage<>(found))
        .when(spy)
        .getPage(
            eq(SEARCH_URI),
            any(),
            refEq(RequestParameters.init().set(FACILITY_ID, facility)));

    SupervisoryNodeDto foundNode = spy.findSupervisoryNode(facility);

    assertEquals(supervisoryNode, foundNode);
    assertNotEquals(secondNode, foundNode);
  }
}