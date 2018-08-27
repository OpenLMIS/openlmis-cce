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

package org.openlmis.cce.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.openlmis.cce.InventoryItemDataBuilder;
import org.openlmis.cce.testutil.ToStringTestUtils;

public class InventoryItemTest {

  @Test
  public void shouldReturnTrueIfStatusHasChanged() {
    InventoryItem inventoryItem = new InventoryItemDataBuilder()
        .withStatus(FunctionalStatus.AWAITING_REPAIR)
        .build();
    InventoryItem inventoryItem2 = new InventoryItemDataBuilder()
        .withStatus(FunctionalStatus.FUNCTIONING)
        .build();

    assertTrue(inventoryItem2.statusChanged(inventoryItem));
  }

  @Test
  public void shouldReturnFalseIfStatusHasNotChanged() {
    InventoryItem inventoryItem = new InventoryItemDataBuilder()
        .withStatus(FunctionalStatus.AWAITING_REPAIR)
        .build();
    InventoryItem inventoryItem2 = new InventoryItemDataBuilder()
        .withStatus(FunctionalStatus.AWAITING_REPAIR)
        .build();

    assertFalse(inventoryItem2.statusChanged(inventoryItem));
  }

  @Test
  public void shouldReturnFalseIfOldInventoryIsNull() {
    InventoryItem inventoryItem2 = new InventoryItemDataBuilder().build();

    assertFalse(inventoryItem2.statusChanged(null));
  }

  @Test
  public void shouldUpdateInventory() {
    InventoryItem existing = new InventoryItemDataBuilder().build();

    InventoryItemDataBuilder differentVariantFieldsBuilder =
        new InventoryItemDataBuilder().withDifferentVariantFields();
    InventoryItem expected = differentVariantFieldsBuilder.build();
    InventoryItem newInventory = differentVariantFieldsBuilder
        .withDifferentInVariantFields()
        .build();

    existing.updateFrom(newInventory);

    assertEquals(expected, existing);
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(InventoryItem.class).withRedefinedSuperclass().verify();
  }

  @Test
  public void shouldImplementToString() {
    InventoryItem inventoryItem = new InventoryItemDataBuilder().build();
    ToStringTestUtils.verify(InventoryItem.class, inventoryItem);
  }
}