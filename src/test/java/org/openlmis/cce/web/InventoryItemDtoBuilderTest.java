package org.openlmis.cce.web;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.FacilityDto;
import org.openlmis.cce.service.referencedata.FacilityReferenceDataService;
import org.openlmis.cce.service.referencedata.UserReferenceDataService;

import java.util.Collections;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class InventoryItemDtoBuilderTest {

  @Mock
  private InventoryItem inventoryItem;

  @Mock
  private FacilityDto facilityDto;

  @Mock
  private FacilityReferenceDataService facilityReferenceDataService;

  @Mock
  private UserReferenceDataService userReferenceDataService;

  @InjectMocks
  private InventoryItemDtoBuilder builder;

  @Before
  public void setUp() throws Exception {
    when(facilityDto.getId()).thenReturn(UUID.randomUUID());
    when(inventoryItem.getFacilityId()).thenReturn(UUID.randomUUID());
    when(inventoryItem.getLastModifierId()).thenReturn(UUID.randomUUID());
  }

  @Test
  public void shouldBuildDtoForList() throws Exception {
    builder.build(Collections.singletonList(inventoryItem), Collections.singletonList(facilityDto));

    verify(facilityReferenceDataService).findOne(inventoryItem.getFacilityId());
    verify(userReferenceDataService).findOne(inventoryItem.getLastModifierId());
  }

  @Test
  public void shouldBuildDto() throws Exception {
    builder.build(inventoryItem);

    verify(facilityReferenceDataService).findOne(inventoryItem.getFacilityId());
    verify(userReferenceDataService).findOne(inventoryItem.getLastModifierId());
  }

  @Test
  public void shouldUseFacilityFromListIfIdsMatch() throws Exception {
    UUID facilityId = UUID.randomUUID();

    when(facilityDto.getId()).thenReturn(facilityId);
    when(inventoryItem.getFacilityId()).thenReturn(facilityId);

    builder.build(inventoryItem, Collections.singletonList(facilityDto));

    verify(facilityReferenceDataService, never()).findOne(any(UUID.class));
    verify(userReferenceDataService).findOne(inventoryItem.getLastModifierId());
  }
}