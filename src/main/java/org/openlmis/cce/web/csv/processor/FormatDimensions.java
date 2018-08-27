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

import org.apache.commons.lang3.StringUtils;
import org.openlmis.cce.domain.Dimensions;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

/**
 * This is a custom cell processor used to format Dimension object to triple.
 * Uses ", " as separator for triple.
 * This is used in CsvCellProcessors.
 */

public class FormatDimensions extends CellProcessorAdaptor implements StringCellProcessor {

  private static final String SEPARATOR = ",";

  @SuppressWarnings("unchecked")
  @Override
  public Object execute(Object value, CsvContext context) {
    validateInputNotNull(value, context);

    String result;
    if (value instanceof Dimensions) {
      Dimensions dimensions = (Dimensions) value;

      if (dimensions.getDepth() == null
          || dimensions.getHeight() == null
          || dimensions.getWidth() == null) {
        throw getSuperCsvCellProcessorException(dimensions, context);
      }

      result = StringUtils.joinWith(SEPARATOR, dimensions.getWidth(),
          dimensions.getDepth(), dimensions.getHeight());
    } else  {
      throw getSuperCsvCellProcessorException(value, context);
    }

    return next.execute(result, context);
  }

  private SuperCsvCellProcessorException getSuperCsvCellProcessorException(Object value,
                                                                           CsvContext context) {
    return new SuperCsvCellProcessorException(
        String.format("'%s' could not be formatted to triple.", value.toString()),
        context, this);
  }
}
