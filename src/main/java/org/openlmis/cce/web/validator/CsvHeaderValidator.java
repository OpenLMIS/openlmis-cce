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

package org.openlmis.cce.web.validator;

import static org.openlmis.cce.i18n.MessageKeys.ERROR_UPLOAD_HEADER_MISSING;
import static org.openlmis.cce.i18n.MessageKeys.ERROR_UPLOAD_MISSING_MANDATORY_COLUMNS;
import static org.openlmis.cce.i18n.MessageKeys.ERROR_UPLOD_INVALID_HEADER;
import static org.openlmis.cce.util.StringHelper.lowerCase;

import org.apache.commons.collections.ListUtils;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.web.upload.ModelClass;
import org.openlmis.cce.web.upload.ModelField;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CsvHeaderValidator {

  /**
   * Validate csv header names.
   */
  public void validateHeaders(List<String> headers,
                              ModelClass modelClass,
                              boolean acceptExtraHeaders) {
    validateNullHeaders(headers);
    List<String> lowerCaseHeaders = lowerCase(headers);
    if (!acceptExtraHeaders) {
      validateInvalidHeaders(lowerCaseHeaders, modelClass);
    }
    validateMandatoryFields(lowerCaseHeaders, modelClass);
  }

  private void validateNullHeaders(List<String> headers) throws ValidationMessageException {
    for (int i = 0; i < headers.size(); i++) {
      if (headers.get(i) == null) {
        String missingHeaderPosition = i + 1 + "";
        throw new ValidationMessageException(ERROR_UPLOAD_HEADER_MISSING, missingHeaderPosition);
      }
    }
  }

  private void validateMandatoryFields(List<String> headers, ModelClass modelClass) {
    List<String> missingFields = findMissingFields(headers, modelClass);

    if (!missingFields.isEmpty()) {
      throw new ValidationMessageException(ERROR_UPLOAD_MISSING_MANDATORY_COLUMNS,
          missingFields.toString());
    }
  }

  private void validateInvalidHeaders(List<String> headers, ModelClass modelClass) {
    List<String> fieldNames = getAllImportedFieldNames(modelClass);
    List invalidHeaders = ListUtils.subtract(headers, lowerCase(fieldNames));
    if (!invalidHeaders.isEmpty()) {
      throw new ValidationMessageException(ERROR_UPLOD_INVALID_HEADER, invalidHeaders.toString());
    }
  }

  private List<String> findMissingFields(List<String> headers, ModelClass modelClass) {
    List<String> missingFields = new ArrayList<>();
    for (ModelField field : modelClass.getImportFields()) {
      if (field.isMandatory()) {
        String fieldName = field.getName();
        if (!headers.contains(fieldName.toLowerCase())) {
          missingFields.add(fieldName);
        }
      }
    }
    return missingFields;
  }

  private List<String> getAllImportedFieldNames(ModelClass modelClass) {
    return modelClass.getImportFields().stream()
        .map(ModelField::getName)
        .collect(Collectors.toList());
  }
}
