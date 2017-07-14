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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openlmis.cce.web.dummy.DummyObject;
import org.openlmis.cce.web.upload.ModelClass;
import org.springframework.util.ReflectionUtils;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class CsvCellProcessorsTest {

  private ModelClass dummyImportableClass;

  @Before
  public void setUp() throws Exception {
    dummyImportableClass = new ModelClass(DummyObject.class);
  }

  @Test
  public void shouldReturnCorrectProcessorForHeaders() {
    List<String> headers =
        Arrays.asList("mandatory string field", "mandatoryIntField", "optionalStringField");

    List<CellProcessor> cellProcessors =
        CsvCellProcessors.getProcessors(dummyImportableClass, headers);

    assertEquals(3, cellProcessors.size());
    assertTrue(cellProcessors.get(0) instanceof NotNull);
    assertTrue(cellProcessors.get(1) instanceof NotNull);
    assertTrue(cellProcessors.get(2) instanceof Optional);
  }


  @Test
  public void testReturnProcessorForMismatchCase() {
    List<String> headers =
        Arrays.asList("MANDAtory String Field", "mandatoryIntFIELD");

    List<CellProcessor> cellProcessors =
        CsvCellProcessors.getProcessors(dummyImportableClass, headers);

    assertEquals(2, cellProcessors.size());
    assertTrue(cellProcessors.get(0) instanceof NotNull);
    assertTrue(cellProcessors.get(1) instanceof NotNull);
  }

  @Test
  public void shouldIgnoreNotAnnotatedHeaders() {
    List<String> headers =
        Arrays.asList("mandatory string field", "mandatoryIntField", "nonAnnotatedField");

    List<CellProcessor> cellProcessors =
        CsvCellProcessors.getProcessors(dummyImportableClass, headers);

    assertEquals(3, cellProcessors.size());
    assertTrue(cellProcessors.get(0) instanceof NotNull);
    assertTrue(cellProcessors.get(1) instanceof NotNull);
    assertNull(cellProcessors.get(2));
  }

  @Test
  public void shouldReturnCorrectNextProcessorForHeaders() throws Exception {
    List<String> headers =
        Arrays.asList("mandatory string field", "OPTIONAL INT FIELD");

    List<CellProcessor> cellProcessors =
        CsvCellProcessors.getProcessors(dummyImportableClass, headers);

    assertEquals(2, cellProcessors.size());

    Field nextProcessorField = getNextProcessorField();

    assertTrue(cellProcessors.get(0) instanceof NotNull);
    NotNull notNull = (NotNull) cellProcessors.get(0);
    assertEquals(getNextProcessorInstanceClass(nextProcessorField, notNull), Trim.class);

    assertTrue(cellProcessors.get(1) instanceof Optional);
    Optional optional = (Optional) cellProcessors.get(1);
    assertEquals(getNextProcessorInstanceClass(nextProcessorField, optional), ParseInt.class);

  }

  private Class<?> getNextProcessorInstanceClass(Field next, CellProcessorAdaptor instance)
      throws Exception {
    return next.get(instance).getClass();
  }

  private Field getNextProcessorField() {
    Field next = ReflectionUtils.findField(CellProcessorAdaptor.class, "next");
    next.setAccessible(true);
    return next;
  }
}