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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.openlmis.cce.dto.RightDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.UUID;

public class RightReferenceDataServiceTest extends BaseReferenceDataServiceTest<RightDto> {
  private static final String RIGHT_NAME = "RIGHT_NAME";

  private final RightReferenceDataService service = new RightReferenceDataService();

  @Override
  protected RightReferenceDataService getService() {
    return service;
  }

  @Override
  protected RightDto generateInstance() {
    RightDto dto = new RightDto();
    dto.setId(UUID.randomUUID());
    dto.setName(RIGHT_NAME);

    return dto;
  }

  @Test
  public void shouldFindRightByName() throws Exception {
    // given
    RightDto instance = generateInstance();

    // when
    mockArrayRequest(HttpMethod.GET, getArrayResultClass(service));
    mockArrayResponse(response -> when(response.getBody()).thenReturn(new Object[]{instance}));

    RightDto found = service.findRight(RIGHT_NAME);

    // then
    assertThat(found, equalTo(instance));

    URI uri = getUri();
    String url = getRequestUrl(service, "search?name=" + RIGHT_NAME);
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();
    assertThat(entity.getBody(), is(nullValue()));
  }

  @Test
  public void shouldReturnFirstIfThereIsMoreElements() throws Exception {
    // given
    RightDto instance1 = generateInstance();
    RightDto instance2 = generateInstance();

    // when
    mockArrayRequest(HttpMethod.GET, getArrayResultClass(service));
    mockArrayResponse(response ->
        when(response.getBody()).thenReturn(new Object[]{instance1, instance2}));

    RightDto found = service.findRight(RIGHT_NAME);

    // then
    assertThat(found, equalTo(instance1));

    URI uri = getUri();
    String url = getRequestUrl(service, "search?name=" + RIGHT_NAME);
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();
    assertThat(entity.getBody(), is(nullValue()));
  }

  @Test
  public void shouldReturnNullIfRightCouldNotBeFound() throws Exception {
    // when
    mockArrayRequest(HttpMethod.GET, getArrayResultClass(service));
    mockArrayResponse(response -> when(response.getBody()).thenReturn(new Object[0]));

    RightDto found = service.findRight(RIGHT_NAME);

    // then
    assertThat(found, is(nullValue()));

    URI uri = getUri();
    String url = getRequestUrl(service, "search?name=" + RIGHT_NAME);
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();
    assertThat(entity.getBody(), is(nullValue()));
  }

}