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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_ITEM_NOT_FOUND;
import static org.openlmis.cce.i18n.PermissionMessageKeys.ERROR_NO_FOLLOWING_PERMISSION;

import com.jayway.restassured.response.Response;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.domain.BackupGeneratorStatus;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.domain.ManualTemperatureGaugeType;
import org.openlmis.cce.domain.ReasonNotWorkingOrNotInUse;
import org.openlmis.cce.domain.Utilization;
import org.openlmis.cce.domain.VoltageRegulatorStatus;
import org.openlmis.cce.domain.VoltageStabilizerStatus;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.openlmis.cce.service.PermissionService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import guru.nidi.ramltester.junit.RamlMatchers;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.TooManyMethods"})
public class InventoryItemControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "/api/inventoryItems";
  private static final String RESOURCE_URL_WITH_ID = RESOURCE_URL + "/{id}";

  @MockBean
  private InventoryItemRepository inventoryItemRepository;

  @MockBean
  private PermissionService permissionService;

  private InventoryItemDto inventoryItemDto;
  private String editPermission = PermissionService.CCE_INVENTORY_EDIT;
  private String viewPermission = PermissionService.CCE_INVENTORY_VIEW;
  private UUID inventoryId = UUID.randomUUID();

  @Before
  public void setUp() {
    mockUserAuthenticated();

    inventoryItemDto = new InventoryItemDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
        "someUniqueId", "eqTrackingId", "abc123", 2010, 2020, "some source",
        FunctionalStatus.FUNCTIONING, true, ReasonNotWorkingOrNotInUse.NOT_APPLICABLE,
        Utilization.ACTIVE, VoltageStabilizerStatus.UNKNOWN, BackupGeneratorStatus.YES,
        VoltageRegulatorStatus.NO, ManualTemperatureGaugeType.BUILD_IN,
        "someMonitorId", "example notes");

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
  public void shouldReturnUnauthorizedWhenPostIfUserHasNoEditInventoryPermission() {
    doThrow(mockPermissionException(editPermission))
        .when(permissionService).canEditInventory();

    postInventoryItem()
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, editPermission)));

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
  public void shouldReturnUnauthorizedWhenGetAllIfUserHasNoViewInventoryPermission() {
    doThrow(mockPermissionException(viewPermission))
        .when(permissionService).canViewInventory();

    getAllInventoryItems()
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, viewPermission)));

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
  public void shouldReturn404WhenGetOneIfInventoryItemNotFound() {
    deleteInventoryItem()
        .then()
        .statusCode(404)
        .body(MESSAGE, equalTo(getMessage(ERROR_ITEM_NOT_FOUND)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedWhenGetOneIfUserHasNoViewInventoryPermission() {
    doThrow(mockPermissionException(viewPermission))
        .when(permissionService).canViewInventory();

    getInventoryItem()
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, viewPermission)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldUpdateInventoryItems() {
    InventoryItemDto oldItem = new InventoryItemDto();
    oldItem.setId(UUID.randomUUID());

    InventoryItemDto response = putInventoryItem(oldItem.getId())
        .then()
        .statusCode(200)
        .extract().as(InventoryItemDto.class);

    assertEquals(oldItem.getId(), response.getId());
    assertEquals(response, inventoryItemDto);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedWhenPutIfUserHasNoEditInventoryPermission() {
    doThrow(mockPermissionException(editPermission))
        .when(permissionService).canEditInventory();

    putInventoryItem(UUID.randomUUID())
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, editPermission)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldDeleteInventoryItemWhenFoundById() {
    when(inventoryItemRepository.findOne(inventoryId))
        .thenReturn(InventoryItem.newInstance(inventoryItemDto));

    deleteInventoryItem()
        .then()
        .statusCode(204);

    verify(inventoryItemRepository).delete(any(InventoryItem.class));
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturn404WhenDeleteIfInventoryItemNotFound() {
    deleteInventoryItem()
        .then()
        .statusCode(404)
        .body(MESSAGE, equalTo(getMessage(ERROR_ITEM_NOT_FOUND)));

    verify(inventoryItemRepository, times(0)).delete(any(InventoryItem.class));
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedWhenDeleteIfUserHasNoEditInventoryPermission() {
    doThrow(mockPermissionException(editPermission))
        .when(permissionService).canEditInventory();

    deleteInventoryItem()
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, editPermission)));

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
        .when()
        .get(RESOURCE_URL);
  }

  private Response getInventoryItem() {
    return restAssured
        .given()
        .queryParam(ACCESS_TOKEN, getToken())
        .pathParam("id", UUID.randomUUID())
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

  private Response deleteInventoryItem() {
    return restAssured
        .given()
        .queryParam(ACCESS_TOKEN, getToken())
        .pathParam("id", inventoryId)
        .when()
        .delete(RESOURCE_URL_WITH_ID);
  }
}