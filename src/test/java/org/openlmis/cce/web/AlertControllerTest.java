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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.domain.Alert;
import org.openlmis.cce.repository.AlertRepository;
import org.openlmis.cce.util.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RunWith(MockitoJUnitRunner.class)
public class AlertControllerTest {

  @Mock
  private AlertRepository alertRepository;
  
  @InjectMocks
  private AlertController alertController;
  
  private PageRequest pageRequest;

  @Before
  public void setUp() {
    pageRequest = new PageRequest(0, 10);
    Page<Alert> alertsPage = Pagination.getPage(Collections.emptyList(), pageRequest);

    when(alertRepository.findByActive(any(Boolean.class), any(Pageable.class)))
        .thenReturn(alertsPage);
    when(alertRepository.findByInventoryItemIdIn(anyListOf(UUID.class), any(Pageable.class)))
        .thenReturn(alertsPage);
    when(alertRepository.findByActiveAndInventoryItemIdIn(
        any(Boolean.class), anyListOf(UUID.class), any(Pageable.class))).thenReturn(alertsPage);
    when(alertRepository.findAll(any(Pageable.class))).thenReturn(alertsPage);
  }

  @Test
  public void getAlertCollectionShouldFindByActiveIfOnlyActiveIsProvided() {
    //when
    alertController.getAlertCollection(null, true, pageRequest);
    
    //then
    verify(alertRepository).findByActive(true, pageRequest);
  }

  @Test
  public void getAlertCollectionShouldFindByInventoryItemsIfOnlyInventoryItemsAreProvided() {
    //given
    List<UUID> inventoryItemIds = Collections.singletonList(UUID.randomUUID());
    
    //when
    alertController.getAlertCollection(inventoryItemIds, null, pageRequest);

    //then
    verify(alertRepository).findByInventoryItemIdIn(inventoryItemIds, pageRequest);
  }

  @Test
  public void getAlertCollectionShouldFindByActiveAndInventoryItemsIfBothAreProvided() {
    //given
    List<UUID> inventoryItemIds = Collections.singletonList(UUID.randomUUID());

    //when
    alertController.getAlertCollection(inventoryItemIds, true, pageRequest);

    //then
    verify(alertRepository).findByActiveAndInventoryItemIdIn(true, inventoryItemIds, pageRequest);
  }

  @Test
  public void getAlertCollectionShouldFindAllIfNeitherAreProvided() {
    //when
    alertController.getAlertCollection(null, null, pageRequest);

    //then
    verify(alertRepository).findAll(pageRequest);
  }
}
