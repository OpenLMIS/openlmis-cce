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

import com.jayway.restassured.response.Response;
import guru.nidi.ramltester.junit.RamlMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.Returns;
import org.openlmis.cce.domain.BackupGeneratorStatus;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.EnergySource;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.domain.ManualTemperatureGaugeType;
import org.openlmis.cce.domain.ReasonNotWorkingOrNotInUse;
import org.openlmis.cce.domain.RemoteTemperatureMonitorType;
import org.openlmis.cce.domain.StorageTemperature;
import org.openlmis.cce.domain.Utilization;
import org.openlmis.cce.domain.VoltageRegulatorStatus;
import org.openlmis.cce.domain.VoltageStabilizerStatus;
import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.dto.ObjectReferenceDto;
import org.openlmis.cce.dto.PermissionStringDto;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.openlmis.cce.service.InventoryStatusProcessor;
import org.openlmis.cce.service.PermissionService;
import org.openlmis.cce.service.PermissionStrings;
import org.openlmis.cce.service.referencedata.FacilityReferenceDataService;
import org.openlmis.cce.service.referencedata.UserReferenceDataService;
import org.openlmis.cce.util.PageImplRepresentation;
import org.openlmis.cce.util.Pagination;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.UUID;

@SuppressWarnings("PMD.TooManyMethods")
public class InventoryItemControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "/api/inventoryItems";
  private static final String RESOURCE_URL_WITH_ID = RESOURCE_URL + "/{id}";

  @MockBean
  private InventoryItemRepository inventoryItemRepository;

  @MockBean
  private FacilityReferenceDataService facilityReferenceDataService;

  @MockBean(name = "userReferenceDataService")
  private UserReferenceDataService userReferenceDataService;

  @MockBean
  private PermissionService permissionService;

  @MockBean
  private InventoryStatusProcessor inventoryStatusProcessor;

  private InventoryItemDto inventoryItemDto;
  private InventoryItem inventoryItem;
  private String editPermission = PermissionService.CCE_INVENTORY_EDIT;
  private String viewPermission = PermissionService.CCE_INVENTORY_VIEW;
  private UUID inventoryId = UUID.randomUUID();
  private final UUID facilityId = UUID.randomUUID();
  private final UUID programId = UUID.randomUUID();
  private ObjectReferenceDto facility = ObjectReferenceDto.ofFacility(facilityId, SERVICE_URL);
  private ObjectReferenceDto userDto = ObjectReferenceDto.ofUser(USER_ID, SERVICE_URL);
  private ObjectReferenceDto program = ObjectReferenceDto.ofUser(programId, SERVICE_URL);
  private CatalogItemDto catalogItemDto = new CatalogItemDto();

  @Before
  public void setUp() {
    mockUserAuthenticated();

    CatalogItem catalogItem = new CatalogItem();
    catalogItem.setFromPqsCatalog(true);
    catalogItem.setType("type");
    catalogItem.setModel("model");
    catalogItem.setManufacturer("manufacturer");
    catalogItem.setEnergySource(EnergySource.ELECTRIC);
    catalogItem.setStorageTemperature(StorageTemperature.MINUS10);
    catalogItem.setArchived(false);

    catalogItemDto = new CatalogItemDto();
    catalogItem.export(catalogItemDto);

    inventoryItemDto = new InventoryItemDto(null,
        facility, catalogItemDto, program, "eqTrackingId",
        "Some Reference Name", 2010, 2020, "some source",
        FunctionalStatus.FUNCTIONING, ReasonNotWorkingOrNotInUse.NOT_APPLICABLE,
        Utilization.ACTIVE, VoltageStabilizerStatus.UNKNOWN, BackupGeneratorStatus.YES,
        VoltageRegulatorStatus.NO, ManualTemperatureGaugeType.BUILD_IN,
        RemoteTemperatureMonitorType.BUILD_IN, "someMonitorId", "example notes", null, null, userDto
    );

    inventoryItem = InventoryItem.newInstance(inventoryItemDto, USER_ID);

    given(inventoryItemRepository.save(any(InventoryItem.class)))
        .willAnswer(new SaveAnswer<InventoryItem>());

    given(facilityReferenceDataService.findAll()).willAnswer(new Returns(singletonList(facility)));
    given(facilityReferenceDataService.findOne(any(UUID.class))).willAnswer(new Returns(facility));

    given(userReferenceDataService.findAll()).willAnswer(new Returns(singletonList(userDto)));
    given(userReferenceDataService.findOne(any(UUID.class))).willAnswer(new Returns(userDto));
  }

  @Test
  public void shouldCreateNewInventoryItem() {
    InventoryItemDto response = postInventoryItem()
        .then()
        .statusCode(201)
        .extract().as(InventoryItemDto.class);

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
    when(inventoryItemRepository.findOne(inventoryId))
        .thenReturn(InventoryItem.newInstance(inventoryItemDto, USER_ID));

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
    InventoryItem existingItem = inventoryItem;
    when(inventoryItemRepository.findOne(inventoryId))
        .thenReturn(existingItem);
    doThrow(mockPermissionException(viewPermission))
        .when(permissionService).canViewInventory(existingItem);

    getInventoryItem()
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, viewPermission)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveAllInventoryItems() {
    UUID userId = mockUser();
    UUID programId = UUID.randomUUID();
    UUID facilityId = UUID.randomUUID();

    PermissionStringDto permission = PermissionStringDto.create(
        CCE_INVENTORY_VIEW, facilityId, programId
    );

    PermissionStrings.Handler handler = mock(PermissionStrings.Handler.class);
    when(handler.get()).thenReturn(singletonList(permission.toString()));

    when(permissionService.getPermissionStrings(userId)).thenReturn(handler);

    when(inventoryItemRepository.search(
        eq(singleton(facilityId)),
        eq(singleton(programId)),
        any(Pageable.class)))
        .thenReturn(Pagination.getPage(singletonList(inventoryItem), null, 1));

    PageImplRepresentation resultPage = getAllInventoryItems()
        .then()
        .statusCode(200)
        .extract().as(PageImplRepresentation.class);

    assertEquals(1, resultPage.getContent().size());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldNotUpdateInvariantInventoryItemFieldsIfInventoryExists() {
    InventoryItemDto existing = new InventoryItemDto(null,
        facility, catalogItemDto, program, "eqTrackingId2",
        "Some Reference Name", 2005, 2025, "some other source",
        FunctionalStatus.NON_FUNCTIONING, ReasonNotWorkingOrNotInUse.DEAD,
        Utilization.NOT_IN_USE, VoltageStabilizerStatus.UNKNOWN,
        BackupGeneratorStatus.NOT_APPLICABLE, VoltageRegulatorStatus.NOT_APPLICABLE,
        ManualTemperatureGaugeType.NO_GAUGE, RemoteTemperatureMonitorType.BUILD_IN,
        "someMonitorId2", "other example notes", null, null, null
    );

    InventoryItem existingItem = InventoryItem.newInstance(existing, null);
    when(inventoryItemRepository.findOne(inventoryId)).thenReturn(existingItem);

    InventoryItemDto response = putInventoryItem(inventoryId)
        .then()
        .statusCode(200)
        .extract().as(InventoryItemDto.class);

    assertEquals(inventoryId, response.getId());
    assertEquals(existing.getFacility().getId(), response.getFacility().getId());
    assertEquals(existing.getProgramId(), response.getProgramId());
    assertEquals(existing.getCatalogItem(), response.getCatalogItem());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldUpdateInventoryItems() {
    InventoryItemDto response = putInventoryItem(inventoryId)
        .then()
        .statusCode(200)
        .extract().as(InventoryItemDto.class);

    assertEquals(inventoryId, response.getId());
    checkResponseAndRaml(response);

    verify(inventoryStatusProcessor, times(1)).functionalStatusChange(any());
  }

  @Test
  public void shouldCallStatusProcessorIfFunctionalStatusDifferWhenUpdateInventoryItems() {
    InventoryItem existing = new InventoryItem();
    existing.setFunctionalStatus(FunctionalStatus.FUNCTIONING);
    when(inventoryItemRepository.findOne(any(UUID.class)))
        .thenReturn(existing);

    inventoryItemDto.setFunctionalStatus(FunctionalStatus.NON_FUNCTIONING);

    putInventoryItem(inventoryId)
        .then()
        .statusCode(200);

    verify(inventoryStatusProcessor, times(1)).functionalStatusChange(any());
  }

  @Test
  public void shouldNotCallStatusProcessorIfFunctionalStatusSameWhenUpdateInventoryItems() {
    InventoryItem existing = new InventoryItem();
    existing.setFunctionalStatus(FunctionalStatus.FUNCTIONING);
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
        .thenReturn(InventoryItem.newInstance(inventoryItemDto, null));

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

  private Response getAllInventoryItems() {
    return restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .when()
        .get(RESOURCE_URL);
  }

  private Response getInventoryItem() {
    return restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam("id", inventoryId)
        .when()
        .get(RESOURCE_URL_WITH_ID);
  }

  private Response putInventoryItem(UUID id) {
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