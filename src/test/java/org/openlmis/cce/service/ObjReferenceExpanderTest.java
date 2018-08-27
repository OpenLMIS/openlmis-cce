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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.javers.common.collections.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.service.dto.ExpandedObjectReferenceDto;
import org.openlmis.cce.service.dto.TestDto;
import org.openlmis.cce.service.dto.TestDtoDataBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("PMD.UnusedPrivateField")
public class ObjReferenceExpanderTest {

  private static final String EXPANDED_STRING_VALUE = "property1";
  private static final List<String> EXPANDED_LIST_VALUE = Lists.asList("element1", "element2");
  private static final UUID EXPANDED_UUID_VALUE = UUID.randomUUID();
  private static final String EXPANDED_OBJECT_REFERENCE_DTO_FIELD = "expandedObjectReferenceDto";

  @Mock
  private AuthService authService;

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private ObjReferenceExpander objReferenceExpander = new ObjReferenceExpander();

  private TestDto testDto;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    objReferenceExpander.registerConverters(); // This is normally called by Spring's @PostConstruct
    testDto = new TestDtoDataBuilder().buildDtoWithObjectReferenceNotExpanded();
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldThrowExceptionIfExpandedFieldIsNotObjectReferenceDto() {
    objReferenceExpander.expandDto(testDto, Lists.asList("uuidProperty"));
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldThrowExceptionIfExpandedFieldDoesNotExist() {
    objReferenceExpander.expandDto(testDto, Lists.asList("nonExistingField"));
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldThrowExceptionIfExpandedFieldDoesNotHaveHrefPropertySet() {
    testDto = new TestDtoDataBuilder().buildDtoWithEmptyObjectReference();
    objReferenceExpander.expandDto(testDto, Lists.asList(EXPANDED_OBJECT_REFERENCE_DTO_FIELD));
  }

  @Test
  public void shouldNotFailIfResourceDoesNotExist() {
    when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(RequestEntity.class),
        eq(Map.class))).thenReturn(ResponseEntity.notFound().build());

    objReferenceExpander.expandDto(testDto, Lists.asList(EXPANDED_OBJECT_REFERENCE_DTO_FIELD));

    assertNotNull(testDto);
    assertNotNull(testDto.getExpandedObjectReferenceDto());
    // Original properties of the DTO should not be lost
    assertNotNull(testDto.getExpandedObjectReferenceDto().getHref());
    assertNotNull(testDto.getExpandedObjectReferenceDto().getId());

    // No expanded properties should be set
    assertNull(testDto.getExpandedObjectReferenceDto().getExpandedStringProperty());
    assertNull(testDto.getExpandedObjectReferenceDto().getExpandedListProperty());
    assertNull(testDto.getExpandedObjectReferenceDto().getExpandedUuidProperty());
  }

  @Test
  public void shouldExpandDto() {
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("expandedStringProperty", EXPANDED_STRING_VALUE);
    responseMap.put("expandedListProperty", EXPANDED_LIST_VALUE);
    responseMap.put("expandedUuidProperty", EXPANDED_UUID_VALUE);

    when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(RequestEntity.class),
        eq(Map.class))).thenReturn(ResponseEntity.ok(responseMap));

    objReferenceExpander.expandDto(testDto, Lists.asList(EXPANDED_OBJECT_REFERENCE_DTO_FIELD));

    ExpandedObjectReferenceDto actual = testDto.getExpandedObjectReferenceDto();
    assertNotNull(actual);

    assertNotNull(actual.getExpandedStringProperty());
    assertEquals(EXPANDED_STRING_VALUE, actual.getExpandedStringProperty());

    assertNotNull(actual.getExpandedListProperty());
    assertEquals(2, actual.getExpandedListProperty().size());
    assertEquals(EXPANDED_LIST_VALUE, actual.getExpandedListProperty());

    assertNotNull(actual.getExpandedUuidProperty());
    assertEquals(EXPANDED_UUID_VALUE, actual.getExpandedUuidProperty());
  }
}
