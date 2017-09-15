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

import org.openlmis.cce.web.csv.model.ModelField;
import org.openlmis.cce.web.csv.model.ModelClass;
import org.supercsv.cellprocessor.FmtBool;
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
  public static final String DIMENSIONS_TYPE = "triple";

  private static final Map<String, CellProcessor> typeParseMappings = new HashMap<>();
  private static final Map<String, CellProcessor> typeExportMappings = new HashMap<>();
  private static final String FORMAT = "dd/MM/yyyy";

  static {
    typeParseMappings.put(INT_TYPE, new ParseInt());
    typeParseMappings.put(LONG_TYPE, new ParseLong());
    typeParseMappings.put(BOOLEAN_TYPE, new ParseBool(true));
    typeParseMappings.put(DOUBLE_TYPE, new ParseDouble());
    typeParseMappings.put(INT_FROM_DOUBLE_TYPE, new ParseIntegerFromDouble());
    typeParseMappings.put(DATE_TYPE,
        new StrRegEx("^\\d{1,2}/\\d{1,2}/\\d{4}$", new ParseDate(FORMAT)));
    typeParseMappings.put(STRING_TYPE, new Trim());
    typeParseMappings.put(BIG_DECIMAL_TYPE, new ParseBigDecimal());
    typeParseMappings.put(ENERGY_SOURCE_TYPE, new ParseEnergySource());
    typeParseMappings.put(STORAGE_TEMPERATURE_TYPE, new ParseStorageTemperature());
    typeParseMappings.put(DIMENSIONS_TYPE, new ParseDimensions());

    typeExportMappings.put(BOOLEAN_TYPE, new FmtBool("Y", "N"));
    typeExportMappings.put(DIMENSIONS_TYPE, new FormatDimensions());
  }

  /**
   * Get all parse processors for given headers.
   */
  public static List<CellProcessor> getParseProcessors(ModelClass modelClass,
                                                       List<String> headers) {
    return getProcessors(modelClass, headers, true);
  }

  /**
   * Get all format processors for given headers.
   */
  public static List<CellProcessor> getFormatProcessors(ModelClass modelClass,
                                                        List<String> headers) {
    return getProcessors(modelClass, headers, false);
  }

  private static List<CellProcessor> getProcessors(ModelClass modelClass,
                                                   List<String> headers,
                                                   boolean forParsing) {
    List<CellProcessor> processors = new ArrayList<>();
    for (String header : headers) {
      ModelField field = modelClass.findImportFieldWithName(header);
      CellProcessor processor = null;
      if (field != null) {
        processor = chainTypeProcessor(field, forParsing);
      }
      processors.add(processor);
    }
    return processors;
  }

  private static CellProcessor chainTypeProcessor(ModelField field, boolean forParsing) {
    CellProcessor mappedProcessor;
    if (forParsing && typeParseMappings.containsKey(field.getType())) {
      mappedProcessor = typeParseMappings.get(field.getType());
    } else if (!forParsing && typeExportMappings.containsKey(field.getType())) {
      mappedProcessor = typeExportMappings.get(field.getType());
    } else {
      mappedProcessor = new Trim();
    }

    return field.isMandatory() ? new NotNull(mappedProcessor) : new Optional(mappedProcessor);
  }
}
