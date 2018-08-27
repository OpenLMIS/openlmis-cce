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
import static org.junit.Assert.assertNull;

import java.time.ZonedDateTime;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

public class AlertDtoTest {

  private AlertDto alertDto;
  
  @Before
  public void setUp() {
    alertDto = new AlertDto();
  }
  
  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(AlertDto.class)
        // AlertDto cannot be final because then we cannot mock it
        .suppress(Warning.STRICT_INHERITANCE)
        // AlertDto fields cannot be final
        .suppress(Warning.NONFINAL_FIELDS)
        .withIgnoredFields("inventoryItemRepository")
        .verify();
  }

  @Test
  public void setEndTimestampShouldCopyToEndTsIfNotNull() {
    //given
    ZonedDateTime zdtNow = ZonedDateTime.now();

    //when
    alertDto.setEndTimestamp(zdtNow);

    //then
    assertEquals(zdtNow, alertDto.getEndTs());
  }

  @Test
  public void getEndTimestampShouldCopyFromEndTsIfNotNull() {
    //given
    ZonedDateTime zdtNow = ZonedDateTime.now();
    alertDto.setEndTs(zdtNow);
    
    //then
    assertEquals(zdtNow, alertDto.getEndTimestamp());
  }

  @Test
  public void getInventoryItemShouldReturnNullIfRepositoryIsNull() {
    //given
    alertDto.setInventoryItemRepository(null);

    //then
    assertNull(alertDto.getInventoryItem());
  }
}
