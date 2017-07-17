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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseIntegerFromDoubleTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private CsvContext context;
  private ParseIntegerFromDouble parseIntegerFromDouble;

  @Before
  public void setUp() throws Exception {
    parseIntegerFromDouble = new ParseIntegerFromDouble();
  }

  @Test
  public void shouldParseIntegerFromDouble() throws Exception {
    int execute = (int)parseIntegerFromDouble.execute("99999999.99", context);

    assertEquals(99999999, execute);
  }

  @Test
  public void shouldParseStringInteger() throws Exception {
    int execute = (int)parseIntegerFromDouble.execute("999", context);

    assertEquals(999, execute);
  }

  @Test
  public void shouldParseInteger() throws Exception {
    int value = 999;
    int execute = (int)parseIntegerFromDouble.execute(value, context);

    assertEquals(value, execute);
  }

  @Test
  public void shouldThrowExceptionIfValueIsGreaterThanMaxIntValue() throws Exception {
    expectedException.expect(SuperCsvCellProcessorException.class);
    expectedException.expectMessage("'2147483648' could not be parsed as an Integer");
    parseIntegerFromDouble.execute("2147483648", context);
  }

  @Test
  public void shouldThrowExceptionIfValueIsNotIntegerNorString() throws Exception {
    expectedException.expect(SuperCsvCellProcessorException.class);
    expectedException.expectMessage("the input value should be of type Integer or String but is"
        + " of type java.lang.Object");

    parseIntegerFromDouble.execute(new Object(), context);
  }

  @Test
  public void shouldThrowExceptionIfValueNotValid() throws Exception {
    expectedException.expect(SuperCsvCellProcessorException.class);
    expectedException.expectMessage("'999,&999.99999999999999' could not be parsed as an Integer");

    parseIntegerFromDouble.execute("999,&999.99999999999999", context);
  }
}