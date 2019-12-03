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

package org.openlmis.cce.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.cce.dto.BaseDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestOperations;

public abstract class ResourceCommunicationServiceTest<T extends BaseDto>
    extends BaseCommunicationServiceTest {

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Override
  protected void initService(RestOperations restClient, AuthService authService) {
    ReflectionTestUtils.setField(getService(), "restTemplate", restClient);
    ReflectionTestUtils.setField(getService(), "authService", authService);
  }

  @Test
  public void shouldFindResource() throws Exception {
    // given
    ResourceCommunicationService<T> service = getService();
    T instance = generateInstance();

    // when
    mockEntityRequest(HttpMethod.GET, getResultClass(service));
    mockEntityResponse(response -> when(response.getBody()).thenReturn(instance));

    T found = service.findById(instance.getId());

    // then
    assertThat(found, equalTo(instance));

    URI uri = getUri();
    String url = getRequestUrl(service, instance.getId().toString());
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();
    assertThat(entity.getBody(), is(nullValue()));
  }

  @Test
  public void shouldReturnNullIfEntityNotFound() throws Exception {
    // given
    ResourceCommunicationService<T> service = getService();
    T instance = generateInstance();
    HttpClientErrorException exp = new HttpClientErrorException(HttpStatus.NOT_FOUND);

    // when
    mockRestClientThrowException(exp);

    T found = service.findById(instance.getId());

    // then
    assertThat(found, is(nullValue()));

    URI uri = getUri();
    String url = getRequestUrl(service, instance.getId().toString());
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();
    assertThat(entity.getBody(), is(nullValue()));
    assertThat(
        entity.getHeaders(),
        hasEntry(AUTHORIZATION, ImmutableList.of(getTokenHeader()))
    );
  }

  @Test
  public void shouldThrowExceptionIfThereIsProblemWithFindingResource() throws Exception {
    // given
    ResourceCommunicationService<T> service = getService();
    T instance = generateInstance();
    HttpServerErrorException exp = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

    // when
    mockRestClientThrowException(exp);
    expectedEx.expect(DataRetrievalException.class);

    service.findById(instance.getId());

    // then
    URI uri = getUri();
    String url = getRequestUrl(service, instance.getId().toString());
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();
    assertThat(entity.getBody(), is(nullValue()));
    assertThat(entity.getHeaders(), hasEntry(AUTHORIZATION, getTokenHeader()));
  }

  @Test
  public void shouldFindResources() throws Exception {
    // given
    ResourceCommunicationService<T> service = getService();
    T instance = generateInstance();

    // when
    mockArrayRequest(HttpMethod.GET, getArrayResultClass(service));
    mockArrayResponse(response -> when(response.getBody()).thenReturn(new Object[]{instance}));

    List<T> found = service.findAll();

    // then
    assertThat(found, hasSize(1));
    assertThat(found.get(0), equalTo(instance));

    URI uri = getUri();
    String url = getRequestUrl(service);
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();
    assertThat(entity.getBody(), is(nullValue()));
  }

  @Test
  public void shouldThrowExceptionIfThereIsProblemWithFindingResources() throws Exception {
    // given
    ResourceCommunicationService<T> service = getService();
    HttpServerErrorException exp = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);

    // when
    mockRestClientThrowException(exp);
    expectedEx.expect(DataRetrievalException.class);

    service.findAll();

    // then
    URI uri = getUri();
    String url = getRequestUrl(service);
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();
    assertThat(entity.getBody(), is(nullValue()));
    assertThat(entity.getHeaders(), hasEntry(AUTHORIZATION, getTokenHeader()));
  }

  protected abstract ResourceCommunicationService<T> getService();

  protected abstract T generateInstance();

  private String getRequestUrl(ResourceCommunicationService<T> service) {
    return service.getServiceUrl() + service.getUrl();
  }

  protected String getRequestUrl(ResourceCommunicationService<T> service, String resourceUrl) {
    return service.getServiceUrl() + service.getUrl() + resourceUrl;
  }

  private Class<T> getResultClass(ResourceCommunicationService<T> service) {
    return service.getResultClass();
  }

  protected Class<T[]> getArrayResultClass(ResourceCommunicationService<T> service) {
    return service.getArrayResultClass();
  }

}