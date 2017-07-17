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

package org.openlmis.cce.web.upload.parser;

import static java.util.Arrays.asList;

import lombok.Getter;
import org.openlmis.cce.domain.BaseEntity;
import org.openlmis.cce.web.upload.model.ModelClass;
import org.openlmis.cce.web.upload.model.ModelField;
import org.openlmis.cce.web.upload.processor.CsvCellProcessors;
import org.openlmis.cce.web.upload.processor.ParseTriple;
import org.openlmis.cce.web.validator.CsvHeaderValidator;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.dozer.CsvDozerBeanReader;
import org.supercsv.prefs.CsvPreference;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.List;

/**
 * This class has responsibility to instantiate a dozerBeanReader from given inputStream,
 * and CsvPreferences. Also is responsible for validating headers.
 */
class CsvBeanReader {

  private ModelClass modelClass;
  private CsvDozerBeanReader dozerBeanReader;
  private CsvHeaderValidator csvHeaderValidator;
  private CellProcessor[] processors;

  @Getter
  private String[] headers;

  CsvBeanReader(ModelClass modelClass,
                InputStream inputStream,
                CsvHeaderValidator csvHeaderValidator) throws IOException {
    this.modelClass = modelClass;
    this.csvHeaderValidator = csvHeaderValidator;
    configureDozerBeanReader(inputStream);
    configureProcessors();
  }

  BaseEntity readWithCellProcessors() throws IOException, NoSuchFieldException,
      IllegalAccessException {
    BaseEntity read = dozerBeanReader.read(modelClass.getClazz(), processors);
    if (read != null) {
      setTriples(read);
    }
    return read;
  }

  int getRowNumber() {
    return dozerBeanReader.getRowNumber();
  }

  void validateHeaders() {
    csvHeaderValidator.validateHeaders(asList(headers), modelClass, false);
  }

  private void setTriples(BaseEntity read) throws IllegalAccessException {
    List<ModelField> triples =
        modelClass.findAllImportFieldsWithType(CsvCellProcessors.TRIPLE_1_TYPE);

    for (ModelField triple : triples) {
      setTripleValues(read, triple);
    }
  }

  private void setTripleValues(BaseEntity read, ModelField triple1) throws IllegalAccessException {
    Field triple1Field = triple1.getField();
    triple1Field.setAccessible(true);
    String tripleValue = (String)triple1Field.get(read);
    if (tripleValue != null) {
      setFieldValue(read, new ParseTriple(1), triple1Field, tripleValue);

      setTriple(read, tripleValue, triple1.getName(), CsvCellProcessors.TRIPLE_2_TYPE,
          new ParseTriple(2));
      setTriple(read, tripleValue, triple1.getName(), CsvCellProcessors.TRIPLE_3_TYPE,
          new ParseTriple(3));
    }
  }

  private void setTriple(BaseEntity read, String tripleValue, String name, String triple2Type,
                         ParseTriple parseTriple) throws IllegalAccessException {
    ModelField triple = modelClass.findImportFieldWithNameAndType(
        name, triple2Type);
    setFieldValue(read, parseTriple, triple.getField(), tripleValue);
  }

  private void setFieldValue(BaseEntity read, ParseTriple parseTriple,
                             Field tripleField, String tripleValue) throws IllegalAccessException {
    tripleField.setAccessible(true);
    tripleField.set(read, parseTriple.execute(tripleValue, null));
  }

  private void configureDozerBeanReader(InputStream inputStream) throws IOException {
    CsvPreference csvPreference = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE)
        .surroundingSpacesNeedQuotes(true)
        .build();

    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
    dozerBeanReader = new CsvDozerBeanReader(bufferedReader, csvPreference);
    headers = readHeaders();
    String[] mappings = modelClass.getFieldNameMappings(headers);
    dozerBeanReader.configureBeanMapping(modelClass.getClazz(), mappings);
  }

  private String[] readHeaders() throws IOException {
    String[] headers = dozerBeanReader.getHeader(true);
    return headers == null ? new String[0] : headers;
  }

  private void configureProcessors() {
    List<CellProcessor> cellProcessors =
        CsvCellProcessors.getProcessors(modelClass, asList(headers));
    processors = cellProcessors.toArray(new CellProcessor[cellProcessors.size()]);
  }
}
