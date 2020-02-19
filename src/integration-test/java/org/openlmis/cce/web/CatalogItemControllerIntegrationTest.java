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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_ID_MISMATCH;
import static org.openlmis.cce.i18n.CsvUploadMessageKeys.ERROR_UPLOAD_MISSING_MANDATORY_COLUMNS;
import static org.openlmis.cce.i18n.CsvUploadMessageKeys.ERROR_UPLOAD_RECORD_INVALID;
import static org.openlmis.cce.i18n.PermissionMessageKeys.ERROR_NO_FOLLOWING_PERMISSION;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import guru.nidi.ramltester.junit.RamlMatchers;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.Dimensions;
import org.openlmis.cce.domain.EnergySource;
import org.openlmis.cce.domain.StorageTemperature;
import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.dto.UploadResultDto;
import org.openlmis.cce.service.PermissionService;
import org.openlmis.cce.util.PageDto;
import org.openlmis.cce.util.Pagination;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@SuppressWarnings({"PMD.UnusedPrivateField", "PMD.TooManyMethods"})
public class CatalogItemControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "/api/catalogItems";
  private static final String RESOURCE_URL_WITH_ID = RESOURCE_URL + "/{id}";
  private static final String FILE_PARAM_NAME = "file";
  private CatalogItemDto catalogItemDto;
  private String managePermission = PermissionService.CCE_MANAGE;


  @Before
  public void setUp() {
    mockUserAuthenticated();
    catalogItemDto = new CatalogItemDto(true, "equipment-code",
        "type", "model", "producent", EnergySource.ELECTRIC, 2016,
        StorageTemperature.MINUS3, 20, -20, "LOW", 1, 1, 1,
        new Dimensions(100, 100, 100), true, false);
    catalogItemDto.setId(UUID.randomUUID());
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
  public void shouldRetrieveAllCatalogItemsWhenCallingSearchWithAllNullParameters() {
    List<CatalogItemDto> items = Collections.singletonList(catalogItemDto);

    when(catalogItemRepository.search(eq(null), eq(null), eq(null), any(Pageable.class)))
        .thenReturn(Pagination.getPage(CatalogItem.newInstance(items), PageRequest.of(1,1), 1));

    PageDto response = getCatalogItems(null, null, null, null, null)
        .then()
        .statusCode(200)
        .extract().as(PageDto.class);

    assertEquals(response.getContent().size(), 1);
    verifyNoMoreInteractions(permissionService);
    verify(catalogItemRepository).search(eq(null), eq(null), eq(null), any(Pageable.class));
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldFindCatalogItemsWithGivenParameters() throws IOException {
    List<CatalogItemDto> items = Collections.singletonList(catalogItemDto);
    when(catalogItemRepository.search(any(String.class), any(Boolean.class),
        any(Boolean.class), any(Pageable.class)))
        .thenReturn(Pagination.getPage(CatalogItem.newInstance(items), PageRequest.of(1,1), 1));

    PageDto response = getCatalogItems("some-type", true, false, 1, 10)
        .then()
        .statusCode(200)
        .extract().as(PageDto.class);

    assertEquals(1, response.getNumberOfElements());
    verifyNoMoreInteractions(permissionService);
    verify(catalogItemRepository).search(eq("some-type"), eq(true), eq(false), any(Pageable.class));
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveCatalogItem() {
    when(catalogItemRepository.findById(any(UUID.class)))
        .thenReturn(Optional.of(CatalogItem.newInstance(catalogItemDto)));

    CatalogItemDto response = getCatalogItem()
        .then()
        .statusCode(200)
        .extract().as(CatalogItemDto.class);

    assertEquals(response, catalogItemDto);
    verifyNoMoreInteractions(permissionService);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldUpdateCatalogItems() {

    CatalogItemDto oldCatalogItem = new CatalogItemDto();
    oldCatalogItem.setId(catalogItemDto.getId());

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
  public void shouldNotUpdateCatalogItemWhenThereIsIdMisMatch() {
    putCatalogItem(UUID.randomUUID())
        .then()
        .statusCode(400)
        .body(MESSAGE_KEY, equalTo(ERROR_ID_MISMATCH))
        .extract().as(CatalogItemDto.class);

    // then
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
        new ClassPathResource("csv/catalogItems/csvWithBasicColumns.csv");

    UploadResultDto result = upload(basicCsvToUpload)
        .then()
        .statusCode(200)
        .extract().as(UploadResultDto.class);

    verify(catalogItemRepository).saveAll(anyList());
    assertEquals(1, result.getAmount().intValue());
    // changed to responseChecks because file parameter is required
    // and RAML check does not recognizes it in request
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.responseChecks());
  }

  @Test
  public void shouldUploadCsvWithAllPossibleFields() throws IOException {
    ClassPathResource fullCsvToUpload =
        new ClassPathResource("csv/catalogItems/csvWithBasicAndOptionalColumns.csv");

    UploadResultDto result = upload(fullCsvToUpload)
        .then()
        .statusCode(200)
        .extract().as(UploadResultDto.class);

    verify(catalogItemRepository).saveAll(anyList());
    assertEquals(1, result.getAmount().intValue());
    // changed to responseChecks because file parameter is required
    // and RAML check does not recognizes it in request
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.responseChecks());
  }

  @Test
  public void shouldNotUploadCsvWithoutMandatoryFields() throws IOException {
    ClassPathResource basicCsvToUpload =
        new ClassPathResource("csv/catalogItems/csvWithMissingMandatoryColumns.csv");

    upload(basicCsvToUpload)
        .then()
        .statusCode(400)
        .body(MESSAGE, equalTo(getMessage(
            ERROR_UPLOAD_MISSING_MANDATORY_COLUMNS, "[From PQS catalog, Archived]")));

    verify(catalogItemRepository, never()).saveAll(anyList());
    // changed to responseChecks because file parameter is required
    // and RAML check does not recognizes it in request
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.responseChecks());
  }

  @Test
  public void shouldNotUploadCsvWithInvalidColumnValues() throws IOException {
    // givne
    ClassPathResource basicCsvToUpload =
        new ClassPathResource("csv/catalogItems/csvWithInvalidColumnValues.csv");

    // Error message based on invalid value in given csv file
    String errorMsg = getMessage(
        ERROR_UPLOAD_RECORD_INVALID, 1, "'ELECTRICITY' could not be parsed as an EnergySource");

    // when
    upload(basicCsvToUpload)
        .then()
        .statusCode(400)
        .body(MESSAGE, equalTo(errorMsg));

    // then
    verify(catalogItemRepository, never()).save(any(CatalogItem.class));
    // changed to responseChecks because file parameter is required
    // and RAML check does not recognizes it in request
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.responseChecks());
  }

  @Test
  public void shouldReturnUnauthorizedWhenUploadCsvIfUserHasNoCceManagePermission()
      throws IOException {
    doThrow(mockPermissionException(managePermission))
        .when(permissionService).canManageCce();
    ClassPathResource basicCsvToUpload =
        new ClassPathResource("csv/catalogItems/csvWithBasicColumns.csv");

    upload(basicCsvToUpload)
        .then()
        .statusCode(403)
        .body(MESSAGE, equalTo(getMessage(ERROR_NO_FOLLOWING_PERMISSION, managePermission)));

    // changed to responseChecks because file parameter is required
    // and RAML check does not recognizes it in request
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.responseChecks());
  }

  @Test
  public void shouldDownloadCsvWithAllPossibleFields() throws IOException {
    when(catalogItemRepository.findAll())
        .thenReturn(Arrays.asList(CatalogItem.newInstance(catalogItemDto)));

    String csvContent = download()
        .then()
        .statusCode(200)
        .extract().body().asString();

    verify(catalogItemRepository).findAll();
    assertEquals("From PQS catalog,PQS equipment code,Type,Model,Manufacturer,"
        + "Energy source,Date of prequal,Storage temperature,Max operating temp (degrees C),"
        + "Min operating temp (degrees C),Energy consumption (NA for solar),Holdover time (hours),"
        + "Gross volume,Net volume,Dimensions,Visible in catalog,Archived\r\n"
        + "Y,equipment-code,type,model,producent,ELECTRIC,2016,MINUS3,20,-20,LOW,1,1,1,"
        + "\"100,100,100\",Y,N\r\n", csvContent);
    verifyNoMoreInteractions(permissionService);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldDownloadCsvWithHeadersOnly() throws IOException {
    when(catalogItemRepository.findAll())
        .thenReturn(Collections.emptyList());

    String csvContent = download()
        .then()
        .statusCode(200)
        .extract().body().asString();

    verify(catalogItemRepository).findAll();
    assertEquals("From PQS catalog,PQS equipment code,Type,Model,Manufacturer,"
        + "Energy source,Date of prequal,Storage temperature,Max operating temp (degrees C),"
        + "Min operating temp (degrees C),Energy consumption (NA for solar),Holdover time (hours),"
        + "Gross volume,Net volume,Dimensions,Visible in catalog,Archived\r\n", csvContent);
    verifyNoMoreInteractions(permissionService);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  private Response postCatalogItem() {
    return restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(catalogItemDto)
        .when()
        .post(RESOURCE_URL);
  }

  private Response getCatalogItems(String type, Boolean archived, Boolean visibleInCatalog,
                                   Integer page, Integer size) {
    RequestSpecification request = restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.APPLICATION_JSON_VALUE);

    if (!StringUtils.isEmpty(type)) {
      request = request.param("type", type);
    }
    if (archived != null) {
      request = request.param("archived", archived);
    }
    if (visibleInCatalog != null) {
      request = request.param("visibleInCatalog", visibleInCatalog);
    }
    if (page != null) {
      request = request.param("page", page);
    }
    if (size != null) {
      request = request.param("size", size);
    }

    return request.when()
        .get(RESOURCE_URL);
  }

  private Response getCatalogItem() {
    return restAssured
        .given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .pathParam("id", UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(RESOURCE_URL_WITH_ID);
  }

  private Response putCatalogItem(UUID id) {
    return restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(APPLICATION_JSON)
        .pathParam("id", id)
        .body(catalogItemDto)
        .when()
        .put(RESOURCE_URL_WITH_ID);
  }

  private Response download() {
    return restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType("text/csv")
        .queryParam("format", "csv")
        .when()
        .get(RESOURCE_URL);
  }

  private Response upload(ClassPathResource basicCsvToUpload) throws IOException {
    return restAssured.given()
        .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        .queryParam("format", "csv")
        .multiPart(FILE_PARAM_NAME,
            basicCsvToUpload.getFilename(),
            basicCsvToUpload.getInputStream())
        .when()
        .post(RESOURCE_URL);
  }
}