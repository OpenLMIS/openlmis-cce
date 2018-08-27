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

package org.openlmis.cce.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.openlmis.cce.service.ResourceNames.USERS;

import java.util.UUID;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class InventoryItemDtoTest {

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(InventoryItemDto.class)
        .withRedefinedSuperclass()
        // InventoryItemDto cannot be final because then we cannot mock it
        .suppress(Warning.STRICT_INHERITANCE)
        // InventoryItemDto fields cannot be final
        .suppress(Warning.NONFINAL_FIELDS)
        .withIgnoredFields("serviceUrl")
        .verify();
  }

  @Test
  public void shouldSetLastModifierId() {
    UUID userId = UUID.randomUUID();
    String serviceUrl = "localhost";

    InventoryItemDto dto = new InventoryItemDto();
    dto.setServiceUrl(serviceUrl);
    dto.setLastModifierId(userId);

    assertNotNull(dto.getLastModifier());
    assertEquals(userId, dto.getLastModifierId());
    assertEquals(UserObjectReferenceDto.create(userId, serviceUrl, USERS), dto.getLastModifier());
  }
}
