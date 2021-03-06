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

package org.openlmis.cce.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class LowerCaseStringsTest {

  private static final String STRING_A = "StringA";
  private static final String STRING_B = "StringB";

  @Test
  public void shouldLowerCaseAllStrings() {
    List<String> strings = new LowerCaseStrings(Arrays.asList(STRING_A, STRING_B)).asList();
    assertEquals(STRING_A.toLowerCase(), strings.get(0));
    assertEquals(STRING_B.toLowerCase(), strings.get(1));
  }

}