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

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.InventoryItemDataBuilder;

public class AlertTest {
  
  private static final String ALERT_TYPE = "type";
  private static final String STATUS_LOCALE = "locale";
  private static final String STATUS_MESSAGE = "message";

  private Alert thisAlert;
  private Alert otherAlert;
  private ZonedDateTime zdtNow;
  
  @Before
  public void setUp() {
    thisAlert = Alert.createNew(UUID.randomUUID().toString(), ALERT_TYPE,
        new InventoryItemDataBuilder().build(), ZonedDateTime.now().minusDays(1), null,
        Collections.singletonMap(STATUS_LOCALE, STATUS_MESSAGE), null);
    zdtNow = ZonedDateTime.now();
    otherAlert = Alert.createNew(UUID.randomUUID().toString(), ALERT_TYPE,
        new InventoryItemDataBuilder().build(), ZonedDateTime.now().minusDays(1), zdtNow,
        Collections.singletonMap(STATUS_LOCALE, STATUS_MESSAGE), zdtNow);
  }

  @Test
  public void fillInFromShouldFillInId() {
    //given
    UUID id = UUID.randomUUID();
    otherAlert.setId(id);

    //when
    thisAlert.fillInFrom(otherAlert);

    //then
    assertEquals(id, thisAlert.getId());
  }
  
  @Test
  public void fillInFromShouldFillInEndTimestampIfThisOneIsNull() {
    //when
    thisAlert.fillInFrom(otherAlert);
    
    //then
    assertEquals(zdtNow, thisAlert.getEndTimestamp());
  }

  @Test
  public void fillInFromShouldNotFillInEndTimestampIfThisOneIsNotNull() {
    //given
    thisAlert = Alert.createNew(UUID.randomUUID().toString(), ALERT_TYPE,
        new InventoryItemDataBuilder().build(), ZonedDateTime.now().minusDays(1),
        zdtNow.minusHours(1), Collections.singletonMap(STATUS_LOCALE, STATUS_MESSAGE), null);

    //when
    thisAlert.fillInFrom(otherAlert);

    //then
    assertEquals(zdtNow.minusHours(1), thisAlert.getEndTimestamp());
  }

  @Test
  public void fillInFromShouldFillInDismissTimestampIfThisOneIsNull() {
    //when
    thisAlert.fillInFrom(otherAlert);

    //then
    assertEquals(zdtNow, thisAlert.getDismissTimestamp());
  }

  @Test
  public void fillInFromShouldNotFillInDismissTimestampIfThisOneIsNotNull() {
    //given
    thisAlert = Alert.createNew(UUID.randomUUID().toString(), ALERT_TYPE,
        new InventoryItemDataBuilder().build(), ZonedDateTime.now().minusDays(1), null,
        Collections.singletonMap(STATUS_LOCALE, STATUS_MESSAGE), zdtNow.minusHours(2));

    //when
    thisAlert.fillInFrom(otherAlert);

    //then
    assertEquals(zdtNow.minusHours(2), thisAlert.getDismissTimestamp());
  }
}
