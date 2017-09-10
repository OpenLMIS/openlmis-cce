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

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

/**
 * This is a custom cell processor used to parse integer from string which is parsable the integer
 * part of double. This is used in CsvCellProcessors.
 */

public class ParseIntegerFromDouble extends CellProcessorAdaptor implements StringCellProcessor {

  private static final String DECIMAl_POINT = "\\.";

  @Override
  public Object execute(Object value, CsvContext context) {
    validateInputNotNull(value, context);

    Integer result;
    if (value instanceof Integer) {
      result = (Integer) value;
    } else if (value instanceof String) {
      String stringValue = (String) value;
      try {
        result = Integer.valueOf(stringValue);
      } catch (final NumberFormatException ex) {
        result = parseIntegerPart(value, context, stringValue, ex);
      }
    } else {
      final String actualClassName = value.getClass().getName();
      throw new SuperCsvCellProcessorException(
          String.format("the input value should be of type Integer or String but is of type %s",
              actualClassName), context, this);
    }

    return next.execute(result, context);
  }

  private Integer parseIntegerPart(Object value, CsvContext context, String stringValue,
                                   NumberFormatException ex) {
    Integer result;
    try {
      //Handle if value can be parsed into double
      Double.valueOf(stringValue);

      String integerPart = stringValue.split(DECIMAl_POINT)[0];

      if (integerPart.length() > 10) {
        throw getSuperCsvCellProcessorException(value, context, ex);
      }

      Long longValue = Long.valueOf(integerPart);
      if (longValue > Integer.MAX_VALUE) {
        throw getSuperCsvCellProcessorException(value, context, ex);
      }

      result = longValue.intValue();

    } catch (NumberFormatException e1) {
      throw getSuperCsvCellProcessorException(value, context, e1);

    }
    return result;
  }

  private SuperCsvCellProcessorException getSuperCsvCellProcessorException(
      Object value, CsvContext context, NumberFormatException ex) {
    return new SuperCsvCellProcessorException(
        String.format("'%s' could not be parsed as an Integer", value), context, this, ex);
  }
}
