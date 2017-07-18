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

package org.openlmis.cce.web.upload.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.cce.domain.Dimensions;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseDimensionsTest {

  private static final Integer EXPECTED_1 = 11;
  private static final Integer EXPECTED_2 = 22;
  private static final Integer EXPECTED_3 = 33;

  @Rule
  public final ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private CsvContext csvContext;

  @Test
  public void shouldParseValidDimensions() throws Exception {
    ParseDimensions parseDimensions = new ParseDimensions();
    Dimensions execute = (Dimensions) parseDimensions.execute(
        String.format("%s, %s, %s", EXPECTED_1, EXPECTED_2, EXPECTED_3), csvContext);

    assertEquals(new Dimensions(EXPECTED_1, EXPECTED_2, EXPECTED_3), execute);
  }

  @Test
  public void shouldThrownExceptionWhenParameterIsNotString() {
    expectedEx.expect(SuperCsvCellProcessorException.class);
    expectedEx.expectMessage("'1' could not be parsed as an Dimensions");

    ParseDimensions parseDimensions = new ParseDimensions();
    parseDimensions.execute(1, csvContext);
  }

  @Test
  public void shouldThrownExceptionWhenTriplePartIsNotAnIntegerType() {
    String invalidTriple = String.format("%s, %s, %s", "test", EXPECTED_2, EXPECTED_3);
    expectedEx.expect(SuperCsvCellProcessorException.class);
    expectedEx.expectMessage("'" + invalidTriple + "' could not be parsed as an Dimensions");

    ParseDimensions parseDimensions = new ParseDimensions();
    parseDimensions.execute(invalidTriple, csvContext);
  }

}