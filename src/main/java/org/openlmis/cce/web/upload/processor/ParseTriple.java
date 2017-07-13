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

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

/**
 * This is a custom cell processor used to parse triple to enum typ.
 * This is used in CsvCellProcessors.
 */

public class ParseTriple extends CellProcessorAdaptor implements StringCellProcessor {

  private int partNumber;

  ParseTriple(int partNumber) {
    super();
    this.partNumber = partNumber;
  }

  @Override
  public Object execute(Object value, CsvContext context) {
    validateInputNotNull(value, context);

    Integer result;
    if (value instanceof String) {
      String valueString = String.valueOf(value);
      String[] split = valueString.split(", ");
      try {
        result = Integer.valueOf(split[partNumber]);
      } catch (final NumberFormatException ex) {
        return getSuperCsvCellProcessorException(value, context);
      }
    } else  {
      return getSuperCsvCellProcessorException(value, context);
    }

    return next.execute(result, context);
  }

  private SuperCsvCellProcessorException getSuperCsvCellProcessorException(Object value,
                                                                           CsvContext context) {
    return new SuperCsvCellProcessorException(
        String.format("'%s' could not be parsed as an triple", value), context, this);
  }
}
