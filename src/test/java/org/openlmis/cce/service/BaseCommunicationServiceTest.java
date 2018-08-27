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

import static com.google.common.collect.ImmutableList.of;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.net.URI;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.Getter;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.util.DynamicPageTypeReference;
import org.openlmis.cce.util.PageImplRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseCommunicationServiceTest {
  private static final String TOKEN = UUID.randomUUID().toString();

  @Mock
  private RestOperations restClient;

  @Mock
  private AuthService authService;

  @Captor
  private ArgumentCaptor<URI> uriCaptor;

  @Captor
  private ArgumentCaptor<HttpEntity> entityCaptor;

  @Mock
  @Getter
  private ResponseEntity entityResponse;

  @Mock
  @Getter
  private ResponseEntity arrayResponse;

  @Mock
  @Getter
  private ResponseEntity<PageImplRepresentation> pageResponse;

  @Before
  public void setUp() throws Exception {
    initService(restClient, authService);
    when(authService.obtainAccessToken()).thenReturn(TOKEN);
  }

  protected abstract void initService(RestOperations restClient, AuthService authService);

  <P> void mockEntityRequest(HttpMethod method, Class<P> type) {
    when(restClient.exchange(uriCaptor.capture(), eq(method), entityCaptor.capture(), eq(type)))
        .thenReturn(entityResponse);
  }

  protected <P> void mockArrayRequest(HttpMethod method, Class<P[]> type) {
    when(restClient.exchange(uriCaptor.capture(), eq(method), entityCaptor.capture(), eq(type)))
        .thenReturn(arrayResponse);
  }

  protected void mockPageRequest() {
    mockPageRequest(HttpMethod.POST);
  }

  protected void mockPageRequest(HttpMethod method) {
    when(restClient
        .exchange(uriCaptor.capture(), eq(method),
            entityCaptor.capture(), any(DynamicPageTypeReference.class)))
        .thenReturn(pageResponse);
  }

  void mockRestClientThrowException(HttpStatusCodeException exp) {
    when(restClient
        .exchange(uriCaptor.capture(), any(HttpMethod.class),
            entityCaptor.capture(), any(Class.class)))
        .thenThrow(exp);
  }

  void mockEntityResponse(Consumer<ResponseEntity> action) {
    action.accept(entityResponse);
  }

  protected void mockArrayResponse(Consumer<ResponseEntity> action) {
    action.accept(arrayResponse);
  }

  protected void mockPageResponse(Consumer<ResponseEntity> action) {
    action.accept(pageResponse);
  }

  protected URI getUri() {
    return uriCaptor.getValue();
  }

  protected HttpEntity getEntity() {
    HttpEntity entity = entityCaptor.getValue();
    assertThat(entity.getHeaders(), hasEntry(AUTHORIZATION, of(getTokenHeader())));

    return entity;
  }

  String getTokenHeader() {
    return "Bearer " + TOKEN;
  }

}