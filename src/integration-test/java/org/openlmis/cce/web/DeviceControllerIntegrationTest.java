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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.openlmis.cce.util.Pagination.DEFAULT_PAGE_NUMBER;

import com.google.common.collect.ImmutableList;
import com.jayway.restassured.response.ValidatableResponse;
import guru.nidi.ramltester.junit.RamlMatchers;
import org.junit.Test;
import org.openlmis.cce.InventoryItemDataBuilder;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.service.ResourceNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class DeviceControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "/api/Device";

  @Value("${service.url}")
  private String serviceUrl;

  private Pageable pageable = new PageRequest(DEFAULT_PAGE_NUMBER, 2000);

  @Test
  public void shouldReturnInventoryItemAsDevice() {
    InventoryItem item = new InventoryItemDataBuilder().build();
    given(inventoryItemRepository.findAll(pageable))
        .willReturn(new PageImpl<>(ImmutableList.of(item)));

    ValidatableResponse response = restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(RESOURCE_URL)
        .then()
        .statusCode(200);

    assertLocation(response, item);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  private void assertLocation(ValidatableResponse response, InventoryItem item) {
    response
        .body("", hasSize(1))
        .body("[0].resourceType", is("Device"))
        .body("[0].id", is(item.getId().toString()))
        .body("[0].manufacturer", is(item.getCatalogItem().getManufacturer()))
        .body("[0].model", is(item.getCatalogItem().getModel()))
        .body("[0].location.reference",
            is(serviceUrl + ResourceNames.getLocationPath() + item.getFacilityId()));
  }

}
