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

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.openlmis.cce.dto.SupervisoryNodeDto;
import org.openlmis.cce.util.PageImplRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

public class SupervisoryNodeReferenceDataServiceTest
    extends BaseReferenceDataServiceTest<SupervisoryNodeDto> {

  private final SupervisoryNodeReferenceDataService service =
      new SupervisoryNodeReferenceDataService();

  @Override
  protected SupervisoryNodeReferenceDataService getService() {
    return service;
  }

  @Override
  protected SupervisoryNodeDto generateInstance() {
    SupervisoryNodeDto dto = new SupervisoryNodeDto();
    dto.setId(UUID.randomUUID());

    return dto;
  }

  @Test
  public void shouldFindNodeByFacilityIdAndProgramId() throws Exception {
    // given
    SupervisoryNodeDto instance = generateInstance();
    UUID facility = UUID.randomUUID();
    UUID program = UUID.randomUUID();

    // when
    mockPageRequest(HttpMethod.GET);
    mockPageResponse(response -> {
      PageImplRepresentation<SupervisoryNodeDto> page = new PageImplRepresentation<>();
      page.setContent(singletonList(instance));

      when(response.getBody()).thenReturn(page);
    });

    SupervisoryNodeDto found = service.findSupervisoryNode(facility, program);

    // then
    assertThat(found, equalTo(instance));

    String uri = getUri().toString();
    String url = getRequestUrl(service, "");
    assertThat(uri, startsWith(url));
    assertThat(uri, containsString("facilityId=" + facility));
    assertThat(uri, containsString("programId=" + program));

    HttpEntity entity = getEntity();
    Object body = entity.getBody();

    assertThat(body, is(nullValue()));
  }

  @Test
  public void shouldReturnFirstIfThereIsMoreElements() throws Exception {
    // given
    SupervisoryNodeDto instance1 = generateInstance();
    SupervisoryNodeDto instance2 = generateInstance();
    UUID facility = UUID.randomUUID();
    UUID program = UUID.randomUUID();

    // when
    mockPageRequest(HttpMethod.GET);
    mockPageResponse(response -> {
      PageImplRepresentation<SupervisoryNodeDto> page = new PageImplRepresentation<>();
      page.setContent(Lists.newArrayList(instance1, instance2));

      when(response.getBody()).thenReturn(page);
    });

    SupervisoryNodeDto found = service.findSupervisoryNode(facility, program);

    // then
    assertThat(found, equalTo(instance1));

    String uri = getUri().toString();
    String url = getRequestUrl(service, "");
    assertThat(uri, startsWith(url));
    assertThat(uri, containsString("facilityId=" + facility));
    assertThat(uri, containsString("programId=" + program));

    HttpEntity entity = getEntity();
    Object body = entity.getBody();

    assertThat(body, is(nullValue()));
  }

  @Test
  public void shouldReturnNullIfUserCouldNotBeFound() throws Exception {
    // given
    UUID facility = UUID.randomUUID();
    UUID program = UUID.randomUUID();

    // when
    mockPageRequest(HttpMethod.GET);
    mockPageResponse(response ->
        when(response.getBody()).thenReturn(new PageImplRepresentation<>()));

    SupervisoryNodeDto found = service.findSupervisoryNode(facility, program);

    // then
    assertThat(found, is(nullValue()));

    String uri = getUri().toString();
    String url = getRequestUrl(service, "");
    assertThat(uri, startsWith(url));
    assertThat(uri, containsString("facilityId=" + facility));
    assertThat(uri, containsString("programId=" + program));

    HttpEntity entity = getEntity();
    Object body = entity.getBody();

    assertThat(body, is(nullValue()));
  }
}
