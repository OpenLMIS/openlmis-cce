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
import static org.openlmis.cce.i18n.MessageKeys.ERROR_NO_FOLLOWING_PERMISSION;
import static org.openlmis.cce.i18n.MessageKeys.ERROR_UPLOAD_MISSING_MANDATORY_COLUMNS;

import com.jayway.restassured.response.Response;
import guru.nidi.ramltester.junit.RamlMatchers;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.Dimensions;
import org.openlmis.cce.domain.EnergySource;
import org.openlmis.cce.domain.StorageTemperature;
import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.openlmis.cce.service.PermissionService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.TooManyMethods"})
public class CatalogItemControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "/api/catalogItems";
  private static final String RESOURCE_URL_WITH_ID = RESOURCE_URL + "/{id}";
  private static final String RESOURCE_URL_UPLOAD = RESOURCE_URL + "/upload";
  private static final String FILE_PARAM_NAME = "file";

  @MockBean
  private CatalogItemRepository catalogItemRepository;

  @MockBean
  private PermissionService permissionService;

  private CatalogItemDto catalogItemDto;
  private String managePermission = PermissionService.CCE_MANAGE;

  @Before
  public void setUp() {
    mockUserAuthenticated();

    catalogItemDto = new CatalogItemDto(true, "equipment-code",
        "type", "model", "producent", EnergySource.ELECTRIC, 2016,
        StorageTemperature.MINUS3, 20, -20, "LOW", 1, 1, 1,
        new Dimensions(100, 100, 100), true);

    when(catalogItemRepository.save(any(CatalogItem.class)))
        .thenAnswer(new SaveAnswer<CatalogItem>());
  }

  @Test
  public void shouldCreateNewCatalogItem() {
    CatalogItemDto response = postCatalogItem()
        .then()
        .statusCode(201)
        .extract().as(CatalogItemDto.class);

    assertEquals(catalogItemDto, response);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedWhenPostIfUserHasNoCceManagePermission() {
    doThrow(mockPermissionException(managePermission))
        .when(permissionService).canManageCce();

    postCatalogItem()
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, managePermission)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveAllCatalogItems() {
    List<CatalogItemDto> items = Collections.singletonList(catalogItemDto);

    when(catalogItemRepository.findAll()).thenReturn(CatalogItem.newInstance(items));

    CatalogItemDto[] response = getAllCatalogItems()
        .then()
        .statusCode(200)
        .extract().as(CatalogItemDto[].class);

    assertEquals(response.length, 1);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedWhenGetAllCatalogItemsIfUserHasNoCceManagePermission() {
    doThrow(mockPermissionException(managePermission))
        .when(permissionService).canManageCce();

    getAllCatalogItems()
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, managePermission)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveCatalogItem() {
    when(catalogItemRepository.findOne(any(UUID.class)))
        .thenReturn(CatalogItem.newInstance(catalogItemDto));

    CatalogItemDto response = getCatalogItem()
        .then()
        .statusCode(200)
        .extract().as(CatalogItemDto.class);

    assertEquals(response, catalogItemDto);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedWhenGetCatalogItemIfUserHasNoCceManagePermission() {
    doThrow(mockPermissionException(managePermission))
        .when(permissionService).canManageCce();

    getCatalogItem()
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, managePermission)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldUpdateCatalogItems() {
    CatalogItemDto oldCatalogItem = new CatalogItemDto();
    oldCatalogItem.setId(UUID.randomUUID());

    CatalogItemDto result = putCatalogItem(oldCatalogItem.getId())
        .then()
        .statusCode(200)
        .extract().as(CatalogItemDto.class);

    // then
    assertEquals(oldCatalogItem.getId(), result.getId());
    assertEquals(catalogItemDto.getEnergySource(), result.getEnergySource());
    assertEquals(catalogItemDto.getEquipmentCode(), result.getEquipmentCode());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedWhenPutIfUserHasNoCceManagePermission() {
    doThrow(mockPermissionException(managePermission))
        .when(permissionService).canManageCce();

    putCatalogItem(UUID.randomUUID())
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, managePermission)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldUploadCsvWithMandatoryFields() throws IOException {
    ClassPathResource basicCsvToUpload =
        new ClassPathResource("csv/catalogItems/basicCsvToUpload.csv");

    upload(basicCsvToUpload)
        .then()
        .statusCode(200)
        .body(equalTo(String.valueOf(1)));

    verify(catalogItemRepository).save(any(CatalogItem.class));
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldUploadCsvWithAllPossibleFields() throws IOException {
    ClassPathResource fullCsvToUpload =
        new ClassPathResource("csv/catalogItems/fullCsvToUpload.csv");

    upload(fullCsvToUpload)
        .then()
        .statusCode(200)
        .body(equalTo(String.valueOf(1)));

    verify(catalogItemRepository).save(any(CatalogItem.class));
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldNotUploadCsvWithoutMandatoryFields() throws IOException {
    ClassPathResource basicCsvToUpload =
        new ClassPathResource("csv/catalogItems/wrongCsvToUpload.csv");

    upload(basicCsvToUpload)
        .then()
        .statusCode(400)
        .body(MESSAGE, equalTo(getMessage(
            ERROR_UPLOAD_MISSING_MANDATORY_COLUMNS, "[From PQS catalog]")));

    verify(catalogItemRepository, times(0)).save(any(CatalogItem.class));
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldReturnUnauthorizedWhenUploadCsvIfUserHasNoCceManagePermission()
      throws IOException {
    doThrow(mockPermissionException(managePermission))
        .when(permissionService).canManageCce();
    ClassPathResource basicCsvToUpload =
        new ClassPathResource("csv/catalogItems/basicCsvToUpload.csv");

    upload(basicCsvToUpload)
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, managePermission)));

    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  private Response postCatalogItem() {
    return restAssured
        .given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(catalogItemDto)
        .when()
        .post(RESOURCE_URL);
  }

  private Response getAllCatalogItems() {
    return restAssured
        .given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(RESOURCE_URL);
  }

  private Response getCatalogItem() {
    return restAssured
        .given()
        .queryParam(ACCESS_TOKEN, getToken())
        .pathParam("id", UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(RESOURCE_URL_WITH_ID);
  }

  private Response putCatalogItem(UUID id) {
    return restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(APPLICATION_JSON)
        .pathParam("id", id)
        .body(catalogItemDto)
        .when()
        .put(RESOURCE_URL_WITH_ID);
  }

  private Response upload(ClassPathResource basicCsvToUpload) throws IOException {
    return restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .multiPart(FILE_PARAM_NAME,
            basicCsvToUpload.getFilename(),
            basicCsvToUpload.getInputStream())
        .when()
        .post(RESOURCE_URL_UPLOAD);
  }
}