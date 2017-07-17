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

import org.openlmis.cce.web.upload.model.ModelField;
import org.openlmis.cce.web.upload.model.ModelClass;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class has mappings from type to cell processors used for parsing value in a cell
 * to corresponding data type.
 */

public class CsvCellProcessors {

  public static final Map<String, CellProcessor> typeMappings = new HashMap<>();
  public static final String INT_TYPE = "int";
  public static final String LONG_TYPE = "long";
  public static final String BOOLEAN_TYPE = "boolean";
  public static final String DOUBLE_TYPE = "double";
  public static final String INT_FROM_DOUBLE_TYPE = "intFromDouble";
  public static final String DATE_TYPE = "Date";
  public static final String STRING_TYPE = "String";
  public static final String BIG_DECIMAL_TYPE = "BigDecimal";
  public static final String ENERGY_SOURCE_TYPE = "EnergySource";
  public static final String STORAGE_TEMPERATURE_TYPE = "StorageTemperature";
  public static final String TRIPLE_1_TYPE = "triple1";
  public static final String TRIPLE_2_TYPE = "triple2";
  public static final String TRIPLE_3_TYPE = "triple3";

  private static final String format = "dd/MM/yyyy";

  static {
    typeMappings.put(INT_TYPE, new ParseInt());
    typeMappings.put(LONG_TYPE, new ParseLong());
    typeMappings.put(BOOLEAN_TYPE, new ParseBool(true));
    typeMappings.put(DOUBLE_TYPE, new ParseDouble());
    typeMappings.put(INT_FROM_DOUBLE_TYPE, new ParseIntegerFromDouble());
    typeMappings.put(DATE_TYPE, new StrRegEx("^\\d{1,2}/\\d{1,2}/\\d{4}$", new ParseDate(format)));
    typeMappings.put(STRING_TYPE, new Trim());
    typeMappings.put(BIG_DECIMAL_TYPE, new ParseBigDecimal());
    typeMappings.put(ENERGY_SOURCE_TYPE, new ParseEnergySource());
    typeMappings.put(STORAGE_TEMPERATURE_TYPE, new ParseStorageTemperature());
  }

  /**
   * Get all processors for given headers.
   */
  public static List<CellProcessor> getProcessors(ModelClass modelClass, List<String> headers) {
    List<CellProcessor> processors = new ArrayList<>();
    for (String header : headers) {
      ModelField field = modelClass.findImportFieldWithName(header);
      CellProcessor processor = null;
      if (field != null) {
        processor = chainTypeProcessor(field);
      }
      processors.add(processor);
    }
    return processors;
  }

  private static CellProcessor chainTypeProcessor(ModelField field) {
    CellProcessor mappedProcessor = typeMappings.get(field.getType());
    return field.isMandatory() ? new NotNull(mappedProcessor) : new Optional(mappedProcessor);
  }
}
