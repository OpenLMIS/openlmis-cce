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

package org.openlmis.cce.web.csv.parser;

import static org.openlmis.cce.i18n.CsvUploadMessageKeys.ERROR_UPLOAD_RECORD_INVALID;

import org.openlmis.cce.dto.BaseDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.util.Message;
import org.openlmis.cce.web.csv.model.ModelClass;
import org.openlmis.cce.web.csv.recordhandler.RecordHandler;
import org.openlmis.cce.web.validator.CsvHeaderValidator;
import org.springframework.stereotype.Component;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;

import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class has logic to invoke corresponding respective record handler to parse data from
 * input stream into the corresponding model.
 */
@Component
@NoArgsConstructor
public class CsvParser {

  /**
   * Parses data from input stream into the corresponding model.
   *
   * @param inputStream   input stream of csv file
   * @param modelClass    java model to which the csv row will be mapped
   * @param recordHandler record persistance handler
   * @return number of uploaded records
   */
  public int process(
      InputStream inputStream, ModelClass modelClass, RecordHandler recordHandler,
      CsvHeaderValidator csvHeaderValidator) throws IOException {

    CsvBeanReader csvBeanReader = new CsvBeanReader(modelClass, inputStream, csvHeaderValidator);
    csvBeanReader.validateHeaders();

    try {
      BaseDto importedModel;
      while ((importedModel = csvBeanReader.readWithCellProcessors()) != null) {
        recordHandler.execute(importedModel);
      }
    } catch (SuperCsvException err) {
      Message message = getCsvRowErrorMessage(err);
      throw new ValidationMessageException(err, message);
    }

    return csvBeanReader.getRowNumber() - 1;
  }

  private Message getCsvRowErrorMessage(SuperCsvException err) {
    CsvContext context = err.getCsvContext();
    int row = context.getRowNumber() - 1;
    return new Message(ERROR_UPLOAD_RECORD_INVALID, row, err.getMessage());
  }

}
