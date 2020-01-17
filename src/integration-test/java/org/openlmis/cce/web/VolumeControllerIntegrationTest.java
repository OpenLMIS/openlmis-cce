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
import static org.mockito.Mockito.when;

import com.jayway.restassured.response.Response;
import guru.nidi.ramltester.junit.RamlMatchers;

import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.dto.VolumeDto;
import org.springframework.http.HttpHeaders;

public class VolumeControllerIntegrationTest extends BaseWebIntegrationTest {

  private static final String RESOURCE_URL = "/api/{facilityId}/volume";

  private VolumeDto volumeDto;
  private final UUID facilityId = UUID.randomUUID();
  private Integer netVolume = 30;

  @Before
  public void setUp() {
    mockUserAuthenticated();
    volumeDto = new VolumeDto(netVolume);
  }

  @Test
  public void shouldRetrieveVolume() {
    when(inventoryItemRepository.getInventoryItemVolume(facilityId, FunctionalStatus.FUNCTIONING))
            .thenReturn(Optional.of(netVolume));

    VolumeDto response = getVolumeForFacilityId(facilityId)
            .then()
            .statusCode(200)
            .extract().as(VolumeDto.class);

    assertEquals(volumeDto, response);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  @Test
  public void shouldRetrieveVolumeWhenRepositoryReturnNullValue() {
    when(inventoryItemRepository.getInventoryItemVolume(facilityId, FunctionalStatus.FUNCTIONING))
            .thenReturn(Optional.empty());

    VolumeDto response = getVolumeForFacilityId(facilityId)
            .then()
            .statusCode(200)
            .extract().as(VolumeDto.class);

    volumeDto.setVolume(0);
    assertEquals(volumeDto, response);
    assertThat(RAML_ASSERT_MESSAGE, restAssured.getLastReport(), RamlMatchers.hasNoViolations());
  }

  private Response getVolumeForFacilityId(UUID facilityId) {
    return restAssured
            .given()
            .header(HttpHeaders.AUTHORIZATION, getTokenHeader())
            .pathParam("facilityId", facilityId)
            .when()
            .get(RESOURCE_URL);
  }
}
