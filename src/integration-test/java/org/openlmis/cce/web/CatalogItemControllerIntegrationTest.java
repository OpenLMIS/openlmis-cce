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

import guru.nidi.ramltester.junit.RamlMatchers;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.EnergySource;
import org.openlmis.cce.domain.StorageTemperature;
import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class CatalogItemControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "/api/catalogItems";
  private static final String RESOURCE_URL_WITH_ID = RESOURCE_URL + "/{id}";

  @MockBean
  private CatalogItemRepository catalogItemRepository;

  private CatalogItemDto catalogItemDto;

  @Before
  public void setUp() {
    mockUserAuthenticated();

    catalogItemDto = new CatalogItemDto(true, "equipment-code",
        "type", "model", "producent", EnergySource.ELECTRICT, 2016,
        StorageTemperature.MINUS3, 20, -20, "LOW", 1, 1, 1, 100, 100, 100, true);

    when(catalogItemRepository.save(any(CatalogItem.class)))
        .thenAnswer(new SaveAnswer<CatalogItem>());
  }

  @Test
  public void shouldCreateNewCatalogItem() {
    CatalogItemDto response = restAssured
        .given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(catalogItemDto)
        .when()
        .post(RESOURCE_URL)
        .then()
        .statusCode(201)
        .extract().as(CatalogItemDto.class);

    assertEquals(catalogItemDto, response);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveAllCatalogItems() {
    List<CatalogItemDto> items = Collections.singletonList(catalogItemDto);

    when(catalogItemRepository.findAll()).thenReturn(CatalogItem.newInstance(items));

    CatalogItemDto[] response = restAssured
        .given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(RESOURCE_URL)
        .then()
        .statusCode(200)
        .extract().as(CatalogItemDto[].class);

    assertEquals(response.length, 1);
    assertEquals(response[0].getDepth(), catalogItemDto.getDepth());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveCatalogItem() {
    when(catalogItemRepository.findOne(any(UUID.class)))
        .thenReturn(CatalogItem.newInstance(catalogItemDto));

    CatalogItemDto response = restAssured
        .given()
        .queryParam(ACCESS_TOKEN, getToken())
        .pathParam("id", UUID.randomUUID())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get(RESOURCE_URL_WITH_ID)
        .then()
        .statusCode(200)
        .extract().as(CatalogItemDto.class);

    assertEquals(response, catalogItemDto);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldUpdateCatalogItems() {
    CatalogItemDto oldCatalogItem = new CatalogItemDto();
    oldCatalogItem.setId(UUID.randomUUID());

    CatalogItemDto result = restAssured.given()
        .queryParam(ACCESS_TOKEN, getToken())
        .contentType(APPLICATION_JSON)
        .pathParam("id", oldCatalogItem.getId())
        .body(catalogItemDto)
        .when()
        .put(RESOURCE_URL_WITH_ID)
        .then()
        .statusCode(200)
        .extract().as(CatalogItemDto.class);

    // then
    assertEquals(oldCatalogItem.getId(), result.getId());
    assertEquals(catalogItemDto.getEnergySource(), result.getEnergySource());
    assertEquals(catalogItemDto.getEquipmentCode(), result.getEquipmentCode());
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }
}