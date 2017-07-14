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

package org.openlmis.cce.web.upload;

import static org.openlmis.cce.i18n.MessageKeys.ERROR_UPLOAD_HEADER_MISSING;
import static org.openlmis.cce.i18n.MessageKeys.ERROR_UPLOAD_MISSING_MANDATORY_COLUMNS;
import static org.openlmis.cce.i18n.MessageKeys.ERROR_UPLOD_INVALID_HEADER;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.ListUtils;
import org.openlmis.cce.domain.BaseEntity;
import org.openlmis.cce.exception.ValidationMessageException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This class represents a Java model to which the csv row is mapped.
 * This class encapsulates validation logic.
 */
@Data
@NoArgsConstructor
public class ModelClass {

  private Class<? extends BaseEntity> clazz;

  private List<ModelField> importFields;
  private boolean acceptExtraHeaders = false;

  public ModelClass(Class<? extends BaseEntity> clazz) {
    this.clazz = clazz;
    importFields = fieldsWithImportFieldAnnotation();
  }

  public ModelClass(Class<? extends BaseEntity> clazz, boolean acceptExtraHeaders) {
    this(clazz);
    this.acceptExtraHeaders = acceptExtraHeaders;
  }

  /**
   * Validate csv header names.
   */
  public void validateHeaders(List<String> headers) {
    validateNullHeaders(headers);
    List<String> lowerCaseHeaders = lowerCase(headers);
    if (!acceptExtraHeaders) {
      validateInvalidHeaders(lowerCaseHeaders);
    }
    validateMandatoryFields(lowerCaseHeaders);
  }

  /**
   * Returns array of field paths.
   */
  public String[] getFieldNameMappings(String[] headers) {
    List<String> fieldMappings = new ArrayList<>();
    for (String header : headers) {
      ModelField importField = findImportFieldWithName(header);
      if (importField != null) {
        String nestedProperty = importField.getNested();
        if (nestedProperty.isEmpty()) {
          fieldMappings.add(importField.getField().getName());
        } else {
          fieldMappings.add(importField.getField().getName() + "." + nestedProperty);
        }
      } else {
        fieldMappings.add(null);
      }

    }
    return fieldMappings.toArray(new String[fieldMappings.size()]);
  }

  /**
   * Returns import name with given name.
   *
   * @param name ImportField name
   * @return import name with given name.
   */
  public ModelField findImportFieldWithName(final String name) {
    Optional<ModelField> fieldOptional = importFields.stream()
        .filter(field -> field.hasName(name))
        .findAny();

    return fieldOptional.orElse(null);
  }

  private List<ModelField> fieldsWithImportFieldAnnotation() {
    List<java.lang.reflect.Field> fieldsList = Arrays.asList(clazz.getDeclaredFields());
    List<ModelField> result = new ArrayList<>();
    for (java.lang.reflect.Field field : fieldsList) {
      if (field.isAnnotationPresent(ImportField.class)) {
        result.add(new ModelField(field, field.getAnnotation(ImportField.class)));
      }
    }

    return result;
  }

  private void validateNullHeaders(List<String> headers) throws ValidationMessageException {
    for (int i = 0; i < headers.size(); i++) {
      if (headers.get(i) == null) {
        String missingHeaderPosition = i + 1 + "";
        throw new ValidationMessageException(ERROR_UPLOAD_HEADER_MISSING, missingHeaderPosition);
      }
    }
  }

  private void validateMandatoryFields(List<String> headers) {
    List<String> missingFields = findMissingFields(headers);

    if (!missingFields.isEmpty()) {
      throw new ValidationMessageException(ERROR_UPLOAD_MISSING_MANDATORY_COLUMNS,
          missingFields.toString());
    }
  }

  private void validateInvalidHeaders(List<String> headers) {
    List<String> fieldNames = getAllImportedFieldNames();
    List invalidHeaders = ListUtils.subtract(headers, lowerCase(fieldNames));
    if (!invalidHeaders.isEmpty()) {
      throw new ValidationMessageException(ERROR_UPLOD_INVALID_HEADER, invalidHeaders.toString());
    }
  }

  private List<String> findMissingFields(List<String> headers) {
    List<String> missingFields = new ArrayList<>();
    for (ModelField field : importFields) {
      if (field.isMandatory()) {
        String fieldName = field.getName();
        if (!headers.contains(fieldName.toLowerCase())) {
          missingFields.add(fieldName);
        }
      }
    }
    return missingFields;
  }

  private List<String> lowerCase(List<String> headers) {
    List<String> lowerCaseHeaders = new ArrayList<>();
    for (String header : headers) {
      lowerCaseHeaders.add(header.toLowerCase());
    }
    return lowerCaseHeaders;
  }

  private List<String> getAllImportedFieldNames() {
    List<String> outputCollection = new ArrayList<>();
    for (ModelField field : importFields) {
      outputCollection.add(field.getName());
    }
    return outputCollection;
  }

}
