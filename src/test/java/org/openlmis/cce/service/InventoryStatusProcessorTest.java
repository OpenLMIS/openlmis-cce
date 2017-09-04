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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.service.notifier.NonfunctionalCceNotifier;

@RunWith(MockitoJUnitRunner.class)
public class InventoryStatusProcessorTest {

  @Mock
  private NonfunctionalCceNotifier nonfunctionalCceNotifier;

  @InjectMocks
  private InventoryStatusProcessor inventoryStatusProcessor;

  private InventoryItem inventoryItem = mock(InventoryItem.class);

  @Test
  public void shouldNotifyWhenItemBecameNonFunctioning() {
    when(inventoryItem.getFunctionalStatus()).thenReturn(FunctionalStatus.NON_FUNCTIONING);

    inventoryStatusProcessor.functionalStatusChange(inventoryItem);

    verify(nonfunctionalCceNotifier).notify(inventoryItem);
  }

  @Test
  public void shouldNotNotifyWhenItemBecameObsolete() {
    when(inventoryItem.getFunctionalStatus()).thenReturn(FunctionalStatus.OBSOLETE);

    inventoryStatusProcessor.functionalStatusChange(inventoryItem);

    verify(nonfunctionalCceNotifier, never()).notify(inventoryItem);
  }

  @Test
  public void shouldNotNotifyWhenItemBecameFunctioning() {
    when(inventoryItem.getFunctionalStatus()).thenReturn(FunctionalStatus.FUNCTIONING);

    inventoryStatusProcessor.functionalStatusChange(inventoryItem);

    verify(nonfunctionalCceNotifier, never()).notify(inventoryItem);
  }
}