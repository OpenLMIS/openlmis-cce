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
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.service.ServiceResponse;
import org.openlmis.cce.util.PageDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserReferenceDataServiceTest extends BaseReferenceDataServiceTest<UserDto> {
  private static final String USER_NAME = "admin";

  private final UserReferenceDataService service = new UserReferenceDataService();

  @Override
  protected UserReferenceDataService getService() {
    return service;
  }

  @Override
  protected UserDto generateInstance() {
    UserDto dto = new UserDto();
    dto.setId(UUID.randomUUID());
    dto.setUsername(USER_NAME);

    return dto;
  }

  @Test
  public void shouldFindUserByUsername() throws Exception {
    // given
    UserDto instance = generateInstance();

    // when
    mockPageRequest();
    mockPageResponse(response -> {
      PageDto<UserDto> page = new PageDto<>();
      page.setContent(singletonList(instance));

      when(response.getBody()).thenReturn(page);
    });

    UserDto found = service.findUser(USER_NAME);

    // then
    assertThat(found, equalTo(instance));

    URI uri = getUri();
    String url = getRequestUrl(service, "search");
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();
    Object body = entity.getBody();

    assertThat(body, instanceOf(Map.class));

    Map<String, Object> map = (Map<String, Object>) body;
    assertThat(map, hasEntry("username", USER_NAME));
  }

  @Test
  public void shouldReturnFirstIfThereIsMoreElements() throws Exception {
    // given
    UserDto instance1 = generateInstance();
    UserDto instance2 = generateInstance();

    // when
    mockPageRequest();
    mockPageResponse(response -> {
      PageDto<UserDto> page = new PageDto<>();
      page.setContent(Lists.newArrayList(instance1, instance2));

      when(response.getBody()).thenReturn(page);
    });

    UserDto found = service.findUser(USER_NAME);

    // then
    assertThat(found, equalTo(instance1));

    URI uri = getUri();
    String url = getRequestUrl(service, "search");
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();
    Object body = entity.getBody();

    assertThat(body, instanceOf(Map.class));

    Map<String, Object> map = (Map<String, Object>) body;
    assertThat(map, hasEntry("username", USER_NAME));
  }

  @Test
  public void shouldReturnNullIfUserCouldNotBeFound() throws Exception {
    // when
    mockPageRequest();
    mockPageResponse(response ->
        when(response.getBody()).thenReturn(new PageDto<>()));

    UserDto found = service.findUser(USER_NAME);

    // then
    assertThat(found, is(nullValue()));

    URI uri = getUri();
    String url = getRequestUrl(service, "search");
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();
    Object body = entity.getBody();

    assertThat(body, instanceOf(Map.class));

    Map<String, Object> map = (Map<String, Object>) body;
    assertThat(map, hasEntry("username", USER_NAME));
  }

  @Test
  public void shouldRetrievePermissionStrings() throws Exception {
    UserDto instance = generateInstance();
    String etag = RandomStringUtils.random(10);

    // when
    mockArrayRequest(HttpMethod.GET, String[].class);
    mockArrayResponse(response ->
        when(response.getBody()).thenReturn(new String[]{"PERMISSION_STRING"}));

    ServiceResponse<List<String>> found = service.getPermissionStrings(instance.getId(), etag);

    // then
    ResponseEntity response = getArrayResponse();

    assertThat(found.getBody(), hasItem("PERMISSION_STRING"));
    assertThat(found.getHeaders(), equalTo(response.getHeaders()));
    assertThat(found.isModified(), is(true));

    URI uri = getUri();
    String url = getRequestUrl(service, instance.getId() + "/permissionStrings");
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();

    assertThat(entity.getBody(), is(nullValue()));
    assertThat(entity.getHeaders(), hasEntry(HttpHeaders.IF_NONE_MATCH, singletonList(etag)));
  }

  @Test
  public void shouldNotRetrievePermissionStringsIfThereWasNoChange() throws Exception {
    UserDto instance = generateInstance();
    String etag = RandomStringUtils.random(10);

    // when
    mockArrayRequest(HttpMethod.GET, String[].class);
    mockArrayResponse(response ->
        when(response.getStatusCode()).thenReturn(HttpStatus.NOT_MODIFIED));

    ServiceResponse<List<String>> found = service.getPermissionStrings(instance.getId(), etag);

    // then
    ResponseEntity response = getArrayResponse();

    assertThat(found.getBody(), is(nullValue()));
    assertThat(found.getHeaders(), equalTo(response.getHeaders()));
    assertThat(found.isModified(), is(false));

    URI uri = getUri();
    String url = getRequestUrl(service, instance.getId() + "/permissionStrings");
    assertThat(uri.toString(), equalTo(url));

    HttpEntity entity = getEntity();

    assertThat(entity.getBody(), is(nullValue()));
    assertThat(entity.getHeaders(), hasEntry(HttpHeaders.IF_NONE_MATCH, singletonList(etag)));
  }

  @Test
  public void shouldFindSupervisingUsers() {
    // given
    UUID rightId = UUID.randomUUID();
    UUID programId = UUID.randomUUID();
    UUID supervisoryNodeId = UUID.randomUUID();
    UserDto instance = generateInstance();
    mockArrayRequest(HttpMethod.GET, getArrayResultClass(service));
    mockArrayResponse(response -> when(response.getBody()).thenReturn(new Object[]{instance}));

    // when

    List<UserDto> users = service
        .findByRight(rightId, programId, supervisoryNodeId);

    // then
    assertThat(users, hasItem(instance));

    URI uri = getUri();
    String url = getRequestUrl(service, "rightSearch");
    assertThat(uri.toString(), startsWith(url));
    assertThat(uri.toString(), containsString("rightId=" + rightId));
    assertThat(uri.toString(), containsString("programId=" + programId));
    assertThat(uri.toString(), containsString("supervisoryNodeId=" + supervisoryNodeId));

    HttpEntity entity = getEntity();
    assertThat(entity.getBody(), is(nullValue()));
  }

  @Test
  public void shouldFindHomeFacilityUsers() {
    // given
    UUID rightId = UUID.randomUUID();
    UUID programId = UUID.randomUUID();
    UserDto instance = generateInstance();
    mockArrayRequest(HttpMethod.GET, getArrayResultClass(service));
    mockArrayResponse(response -> when(response.getBody()).thenReturn(new Object[]{instance}));

    // when

    List<UserDto> users = service
        .findByRight(rightId, programId, null);

    // then
    assertThat(users, hasItem(instance));

    URI uri = getUri();
    String url = getRequestUrl(service, "rightSearch");
    assertThat(uri.toString(), startsWith(url));
    assertThat(uri.toString(), containsString("rightId=" + rightId));
    assertThat(uri.toString(), containsString("programId=" + programId));
    assertThat(uri.toString(), not(containsString("supervisoryNodeId")));

    HttpEntity entity = getEntity();
    assertThat(entity.getBody(), is(nullValue()));
  }
}
