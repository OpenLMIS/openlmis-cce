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

package org.openlmis.cce.web.csv.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.cce.domain.Dimensions;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class FormatDimensionsTest {

  @Rule
  public final ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private CsvContext csvContext;

  private FormatDimensions formatDimensions;

  @Before
  public void beforeEach() {
    formatDimensions = new FormatDimensions();
  }

  @Test
  public void shouldFormatValidDimensions() throws Exception {
    Dimensions dimensions = new Dimensions(10, 20, 30);

    String result = (String) formatDimensions.execute(dimensions, csvContext);

    assertEquals("10,20,30", result);
  }

  @Test
  public void shouldThrownExceptionWhenParameterIsNull() {
    checkDimensionsFormatting(10, 20, null);
    checkDimensionsFormatting(10, null, 30);
    checkDimensionsFormatting(null, 20, 30);
  }

  @Test
  public void shouldThrownExceptionWhenTriplePartIsNotAnIntegerType() {
    String invalidTriple = "invalid-type";

    expectedEx.expect(SuperCsvCellProcessorException.class);
    expectedEx.expectMessage("'" + invalidTriple
        + "' could not be formatted to triple.");

    formatDimensions.execute(invalidTriple, csvContext);
  }

  private void checkDimensionsFormatting(Integer width, Integer depth, Integer height) {
    Dimensions dimensions = new Dimensions(width, depth, height);

    expectedEx.expect(SuperCsvCellProcessorException.class);
    expectedEx.expectMessage(getErrorMessage(dimensions));

    formatDimensions.execute(dimensions, csvContext);
  }

  private String getErrorMessage(Dimensions dimensions) {
    return "'" + dimensions.toString() + "' could not be formatted to triple.";
  }
}
