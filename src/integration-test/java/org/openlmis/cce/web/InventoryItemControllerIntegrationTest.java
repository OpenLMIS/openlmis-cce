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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import com.jayway.restassured.response.Response;
import guru.nidi.ramltester.junit.RamlMatchers;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.domain.BackupGenerator;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.domain.ManualTemperatureGauge;
import org.openlmis.cce.domain.ReasonNotWorkingOrNotInUse;
import org.openlmis.cce.domain.Utilization;
import org.openlmis.cce.domain.VoltageRegulator;
import org.openlmis.cce.domain.VoltageStabilizer;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.i18n.MessageService;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.TooManyMethods"})
public class InventoryItemControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "/api/inventoryItems";
  private static final String RESOURCE_URL_WITH_ID = RESOURCE_URL + "/{id}";

  @MockBean
  private InventoryItemRepository inventoryItemRepository;

  @Autowired
  private MessageService messageService;

  private InventoryItemDto inventoryItemDto;

  @Before
  public void setUp() {
    mockUserAuthenticated();

    inventoryItemDto = new InventoryItemDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "someUniqueId", "eqTrackingId", "abc123", 2010, 2020, "some source",
        FunctionalStatus.FUNCTIONING, true, ReasonNotWorkingOrNotInUse.NOT_APPLICABLE,
        Utilization.ACTIVE, VoltageStabilizer.UNKNOWN, BackupGenerator.YES, VoltageRegulator.NO,
        ManualTemperatureGauge.BUILD_IN, "someMonitorId", "example notes");

    when(inventoryItemRepository.save(any(InventoryItem.class)))
        .thenAnswer(new SaveAnswer<InventoryItem>());
  }

  @Test
  public void shouldCreateNewInventoryItem() {
    InventoryItemDto response = postInventoryItem()
        .then()
        .statusCode(201)
        .extract().as(InventoryItemDto.class);

    assertEquals(inventoryItemDto, response);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveAllInventoryItems() {
    List<InventoryItemDto> items = Collections.singletonList(inventoryItemDto);

    when(inventoryItemRepository.findAll()).thenReturn(InventoryItem.newInstance(items));

    InventoryItemDto[] response = getAllInventoryItems()
        .then()
        .statusCode(200)
        .extract().as(InventoryItemDto[].class);

    assertEquals(response.length, 1);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveInventoryItem() {
    when(inventoryItemRepository.findOne(any(UUID.class)))
        .thenReturn(InventoryItem.newInstance(inventoryItemDto));

    InventoryItemDto response = getInventoryItem()
        .then()
        .statusCode(200)
        .extract().as(InventoryItemDto.class);

    assertEquals(response, inventoryItemDto);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldUpdateInventoryItems() {
    InventoryItemDto oldItem = new InventoryItemDto();
    oldItem.setId(UUID.randomUUID());

    InventoryItemDto result = putInventoryItem(oldItem.getId())
        .then()
        .statusCode(200)
        .extract().as(InventoryItemDto.class);

    // then
    assertEquals(oldItem.getId(), result.getId());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  private Response postInventoryItem() {
    return restAssured
        .given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(inventoryItemDto)
        .when()
        .post(RESOURCE_URL);
  }

  private Response getAllInventoryItems() {
    return restAssured
        .given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(RESOURCE_URL);
  }

  private Response getInventoryItem() {
    return restAssured
        .given()
        .queryParam(ACCESS_TOKEN, getToken())
        .pathParam("id", UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(RESOURCE_URL_WITH_ID);
  }

  private Response putInventoryItem(UUID id) {
    return restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(APPLICATION_JSON)
        .pathParam("id", id)
        .body(inventoryItemDto)
        .when()
        .put(RESOURCE_URL_WITH_ID);
  }
}