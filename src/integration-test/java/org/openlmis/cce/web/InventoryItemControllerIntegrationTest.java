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

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_ITEM_NOT_FOUND;
import static org.openlmis.cce.i18n.PermissionMessageKeys.ERROR_NO_FOLLOWING_PERMISSION;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_VIEW;
import static org.openlmis.cce.service.ResourceNames.FACILITIES;
import static org.openlmis.cce.service.ResourceNames.PROGRAMS;
import static org.openlmis.cce.service.ResourceNames.USERS;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.Returns;
import org.openlmis.cce.CatalogItemDataBuilder;
import org.openlmis.cce.InventoryItemDataBuilder;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.dto.ObjectReferenceDto;
import org.openlmis.cce.dto.PermissionStringDto;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.dto.UserObjectReferenceDto;
import org.openlmis.cce.service.PermissionService;
import org.openlmis.cce.service.PermissionStrings;
import org.openlmis.cce.util.PageImplRepresentation;
import org.openlmis.cce.util.Pagination;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import guru.nidi.ramltester.junit.RamlMatchers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("PMD.TooManyMethods")
public class InventoryItemControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "/api/inventoryItems";
  private static final String RESOURCE_URL_WITH_ID = RESOURCE_URL + "/{id}";
  private static final String EXPAND = "expand";
  private static final String LAST_MODIFIER = "lastModifier";

  private InventoryItemDto inventoryItemDto;
  private InventoryItem inventoryItem;
  private String editPermission = PermissionService.CCE_INVENTORY_EDIT;
  private String viewPermission = PermissionService.CCE_INVENTORY_VIEW;
  private UUID inventoryId = UUID.randomUUID();
  private final UUID facilityId = UUID.randomUUID();
  private final UUID programId = UUID.randomUUID();
  private ObjectReferenceDto facility =
      ObjectReferenceDto.create(facilityId, SERVICE_URL, FACILITIES);
  private UserObjectReferenceDto lastModifier =
      UserObjectReferenceDto.create(USER_ID, SERVICE_URL, USERS);
  private ObjectReferenceDto program = ObjectReferenceDto.create(programId, SERVICE_URL, PROGRAMS);

  @Before
  public void setUp() {
    mockUserAuthenticated();

    inventoryItem = new InventoryItemDataBuilder()
        .withLastModifierId(USER_ID)
        .withFacilityId(facilityId)
        .withProgramId(programId)
        .build();

    inventoryItemDto = toDto(inventoryItem);

    given(inventoryItemRepository.save(any(InventoryItem.class)))
        .willAnswer(new SaveAnswer<InventoryItem>());

    given(facilityReferenceDataService.findAll()).willAnswer(new Returns(singletonList(facility)));
    given(facilityReferenceDataService.findOne(any(UUID.class))).willAnswer(new Returns(facility));
  }

  @Test
  public void shouldCreateNewInventoryItem() {
    inventoryItemDto.setLastModifier(null);
    InventoryItemDto response = postInventoryItem()
        .then()
        .statusCode(201)
        .extract().as(InventoryItemDto.class);

    inventoryItemDto.setLastModifier(lastModifier);
    inventoryItemDto.setId(response.getId());
    checkResponseAndRaml(response);
  }

  @Test
  public void shouldReturnUnauthorizedWhenPostIfUserHasNoEditInventoryPermission() {
    doThrow(mockPermissionException(editPermission))
        .when(permissionService).canEditInventory(any(UUID.class), any(UUID.class));

    postInventoryItem()
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, editPermission)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveInventoryItem() {
    when(inventoryItemRepository.findOne(inventoryId)).thenReturn(inventoryItem);

    InventoryItemDto response = getInventoryItem(false)
        .then()
        .statusCode(200)
        .extract().as(InventoryItemDto.class);

    assertEquals(inventoryItemDto, response);
    verify(objReferenceExpander).expandDto(eq(response), eq(null));
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveInventoryItemWithExpandedLastModifier() {
    when(inventoryItemRepository.findOne(inventoryId)).thenReturn(inventoryItem);

    InventoryItemDto response = getInventoryItem(true)
        .then()
        .statusCode(200)
        .extract().as(InventoryItemDto.class);

    assertEquals(inventoryItemDto, response);
    verify(objReferenceExpander).expandDto(eq(response), eq(Lists.asList("lastModifier")));
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
    InventoryItem existingItem = inventoryItem;
    when(inventoryItemRepository.findOne(inventoryId))
        .thenReturn(existingItem);
    doThrow(mockPermissionException(viewPermission))
        .when(permissionService).canViewInventory(existingItem);

    getInventoryItem(false)
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, viewPermission)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveAllInventoryItemsForGivenParameters() {
    UUID userId = mockUser();
    UUID programId = UUID.randomUUID();
    UUID facilityId = UUID.randomUUID();

    mockUserPermissions(userId, programId, facilityId);

    when(inventoryItemRepository.search(
        eq(singleton(facilityId)),
        eq(singleton(programId)),
        eq(FunctionalStatus.FUNCTIONING),
        any(Pageable.class)))
        .thenReturn(Pagination.getPage(singletonList(inventoryItem), null, 1));

    PageImplRepresentation resultPage = getAllInventoryItems(
        null, null, FunctionalStatus.FUNCTIONING, false)
        .then()
        .statusCode(200)
        .extract().as(PageImplRepresentation.class);

    assertEquals(1, resultPage.getContent().size());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveAllInventoryItemsWithExpandedLastModifier() {
    UUID userId = mockUser();
    UUID programId = UUID.randomUUID();
    UUID facilityId = UUID.randomUUID();

    mockUserPermissions(userId, programId, facilityId);

    when(inventoryItemRepository.search(
        eq(singleton(facilityId)),
        eq(singleton(programId)),
        eq(null),
        any(Pageable.class)))
        .thenReturn(Pagination.getPage(
            Lists.asList(inventoryItem, inventoryItem, inventoryItem),
            null, 3));

    PageImplRepresentation resultPage = getAllInventoryItems(null, null, null, true)
        .then()
        .statusCode(200)
        .extract().as(PageImplRepresentation.class);

    assertEquals(3, resultPage.getContent().size());

    // All 3 DTOs should be expanded
    verify(objReferenceExpander, times(3))
        .expandDto(any(InventoryItemDto.class), eq(Lists.asList(LAST_MODIFIER)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveInventoryItemsForGivenFacility() {
    UUID userId = mockUser();
    UUID programId = UUID.randomUUID();
    UUID facilityId = UUID.randomUUID();

    PermissionStringDto permission1 = PermissionStringDto.create(
        CCE_INVENTORY_VIEW, UUID.randomUUID(), programId
    );
    PermissionStringDto permission2 = PermissionStringDto.create(
        CCE_INVENTORY_VIEW, UUID.randomUUID(), programId
    );
    PermissionStringDto permission3 = PermissionStringDto.create(
        CCE_INVENTORY_VIEW, facilityId, programId
    );
    PermissionStringDto permission4 = PermissionStringDto.create(
        CCE_INVENTORY_VIEW, UUID.randomUUID(), programId
    );

    PermissionStrings.Handler handler = mock(PermissionStrings.Handler.class);
    when(handler.get()).thenReturn(Sets.asSet(permission1, permission2, permission3, permission4));

    when(permissionService.getPermissionStrings(userId)).thenReturn(handler);

    when(inventoryItemRepository.search(
        eq(singleton(facilityId)),
        eq(singleton(programId)),
        eq(null),
        any(Pageable.class)))
        .thenReturn(Pagination.getPage(singletonList(inventoryItem), null, 1));

    PageImplRepresentation resultPage = getAllInventoryItems(facilityId, null, null, false)
        .then()
        .statusCode(200)
        .extract().as(PageImplRepresentation.class);

    assertEquals(1, resultPage.getContent().size());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveInventoryItemsForGivenProgram() {
    UUID userId = mockUser();
    UUID programId = UUID.randomUUID();

    PermissionStringDto permission1 = PermissionStringDto.create(
        CCE_INVENTORY_VIEW, UUID.randomUUID(), programId
    );
    PermissionStringDto permission2 = PermissionStringDto.create(
        CCE_INVENTORY_VIEW, UUID.randomUUID(), programId
    );

    PermissionStrings.Handler handler = mock(PermissionStrings.Handler.class);
    when(handler.get()).thenReturn(Sets.asSet(permission1, permission2));

    when(permissionService.getPermissionStrings(userId)).thenReturn(handler);

    when(inventoryItemRepository.search(
        eq(Sets.asSet(permission1.getFacilityId(), permission2.getFacilityId())),
        eq(singleton(programId)),
        eq(null),
        any(Pageable.class)))
        .thenReturn(Pagination.getPage(singletonList(inventoryItem), null, 1));

    PageImplRepresentation resultPage = getAllInventoryItems(null, programId, null, false)
        .then()
        .statusCode(200)
        .extract().as(PageImplRepresentation.class);

    assertEquals(1, resultPage.getContent().size());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldNotUpdateInvariantInventoryItemFieldsIfInventoryExists() {
    CatalogItem catalogItem = new CatalogItemDataBuilder()
        .withoutFromPqsCatalogFlag()
        .withGasolineEnergySource()
        .withMinusThreeStorageTemperature()
        .withArchiveFlag()
        .build();

    InventoryItem existing = new InventoryItemDataBuilder()
        .withId(inventoryId)
        .withCatalogItem(catalogItem)
        .withProgramId(UUID.randomUUID())
        .withFacilityId(UUID.randomUUID())
        .build();

    when(inventoryItemRepository.findOne(inventoryId)).thenReturn(existing);

    InventoryItemDto response = putInventoryItem(inventoryId)
        .then()
        .statusCode(200)
        .extract().as(InventoryItemDto.class);

    assertEquals(inventoryId, response.getId());
    assertEquals(existing.getFacilityId(), response.getFacilityId());
    assertEquals(existing.getProgramId(), response.getProgramId());

    CatalogItemDto catalogItemDto = new CatalogItemDto();
    catalogItem.export(catalogItemDto);
    assertEquals(catalogItemDto, response.getCatalogItem());

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldCreateInventoryItemAndNotCallStatusNotifier() {
    InventoryItemDto response = putInventoryItem(inventoryId)
        .then()
        .statusCode(200)
        .extract().as(InventoryItemDto.class);

    assertEquals(inventoryId, response.getId());
    checkResponseAndRaml(response);

    verify(inventoryStatusProcessor, never()).functionalStatusChange(any());
  }

  @Test
  public void shouldCallStatusProcessorIfFunctionalStatusDifferWhenUpdateInventoryItems() {
    InventoryItem existing = new InventoryItemDataBuilder()
        .withStatus(FunctionalStatus.FUNCTIONING)
        .build();
    when(inventoryItemRepository.findOne(any(UUID.class)))
        .thenReturn(existing);

    inventoryItemDto.setFunctionalStatus(FunctionalStatus.AWAITING_REPAIR);

    putInventoryItem(inventoryId)
        .then()
        .statusCode(200);

    verify(inventoryStatusProcessor, times(1)).functionalStatusChange(any());
  }

  @Test
  public void shouldNotCallStatusProcessorIfFunctionalStatusSameWhenUpdateInventoryItems() {
    InventoryItem existing = new InventoryItemDataBuilder()
        .withStatus(FunctionalStatus.FUNCTIONING)
        .build();
    when(inventoryItemRepository.findOne(any(UUID.class)))
        .thenReturn(existing);

    inventoryItemDto.setFunctionalStatus(FunctionalStatus.FUNCTIONING);

    putInventoryItem(inventoryId)
        .then()
        .statusCode(200);

    verify(inventoryStatusProcessor, never()).functionalStatusChange(any());
  }

  @Test
  public void shouldReturnUnauthorizedWhenPutIfUserHasNoEditInventoryPermission() {
    doThrow(mockPermissionException(editPermission))
        .when(permissionService)
        .canEditInventory(inventoryItemDto.getProgramId(), inventoryItemDto.getFacility().getId());

    putInventoryItem(UUID.randomUUID())
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, editPermission)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedWhenPutIfUserHasNoEditInventoryPermissionForExistingItem() {
    InventoryItem existingItem = inventoryItem;
    when(inventoryItemRepository.findOne(inventoryId)).thenReturn(existingItem);

    doThrow(mockPermissionException(editPermission))
        .when(permissionService)
        .canEditInventory(existingItem);

    putInventoryItem(inventoryId)
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
    InventoryItem existingItem = inventoryItem;
    when(inventoryItemRepository.findOne(inventoryId))
        .thenReturn(existingItem);
    doThrow(mockPermissionException(editPermission))
        .when(permissionService)
        .canEditInventory(existingItem);

    deleteInventoryItem()
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, editPermission)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  private void mockUserPermissions(UUID userId, UUID programId, UUID facilityId) {
    PermissionStringDto permission = PermissionStringDto.create(
        CCE_INVENTORY_VIEW, facilityId, programId
    );

    PermissionStrings.Handler handler = mock(PermissionStrings.Handler.class);
    when(handler.get()).thenReturn(singleton(permission));

    when(permissionService.getPermissionStrings(userId)).thenReturn(handler);
  }

  private InventoryItemDto toDto(InventoryItem domain) {
    InventoryItemDto dto = new InventoryItemDto();
    domain.export(dto);
    dto.setProgram(program);
    dto.setFacility(facility);
    dto.setLastModifier(lastModifier);

    return dto;
  }

  private void checkResponseAndRaml(InventoryItemDto response) {
    assertNotNull(response.getModifiedDate());
    inventoryItemDto.setModifiedDate(response.getModifiedDate());
    assertEquals(inventoryItemDto.getFacility().getId(), response.getFacility().getId());
    assertEquals(inventoryItemDto.getFacility().getHref(), response.getFacility().getHref());
    assertEquals(inventoryItemDto, response);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  private UUID mockUser() {
    UserDto user = new UserDto();
    user.setId(USER_ID);
    when(authenticationHelper.getCurrentUser()).thenReturn(user);
    return user.getId();
  }

  private Response postInventoryItem() {
    return restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(inventoryItemDto)
        .when()
        .post(RESOURCE_URL);
  }

  private Response getAllInventoryItems(UUID facilityId, UUID programId,
                                        FunctionalStatus functionalStatus, boolean expanded) {
    RequestSpecification request = restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader());

    if (facilityId != null) {
      request = request.queryParam("facilityId", facilityId.toString());
    }

    if (programId != null) {
      request = request.queryParam("programId", programId.toString());
    }

    if (functionalStatus != null) {
      request = request.queryParam("functionalStatus", functionalStatus.toString());
    }

    if (expanded) {
      request = request.queryParam(EXPAND, LAST_MODIFIER);
    }

    return request
        .when()
        .get(RESOURCE_URL);
  }

  private Response getInventoryItem(boolean expanded) {
    Map<String, String> queryParams = new HashMap<>();
    if (expanded) {
      queryParams.put(EXPAND, LAST_MODIFIER);
    }

    return restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .queryParameters(queryParams)
        .pathParam("id", inventoryId)
        .when()
        .get(RESOURCE_URL_WITH_ID);
  }

  private Response putInventoryItem(UUID id) {
    inventoryItemDto.setId(id);
    return restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(APPLICATION_JSON)
        .pathParam("id", id)
        .body(inventoryItemDto)
        .when()
        .put(RESOURCE_URL_WITH_ID);
  }

  private Response deleteInventoryItem() {
    return restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam("id", inventoryId)
        .when()
        .delete(RESOURCE_URL_WITH_ID);
  }
}
