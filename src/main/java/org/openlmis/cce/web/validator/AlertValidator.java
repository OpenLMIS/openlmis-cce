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

import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.openlmis.cce.dto.AlertDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.i18n.AlertMessageKeys;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.ValidationUtils;

@Component
public class AlertValidator {

  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(AlertValidator.class);
  static final String FHIR_REGEX = "[A-Za-z0-9\\-\\.]{1,64}";

  @Autowired
  InventoryItemRepository inventoryItemRepository;
  
  /**
   * Validates the {@code alertDto} object.
   * The method checks if the object has values in all required properties.
   *
   * @param alertDto the object that will be validated
   * @see ValidationUtils
   */
  public void validate(AlertDto alertDto) {
    validateNotNull(alertDto.getAlertId(), AlertMessageKeys.ERROR_ALERT_ID_REQUIRED);
    validateNotNull(alertDto.getAlertType(), AlertMessageKeys.ERROR_ALERT_TYPE_REQUIRED);
    validateNotNull(alertDto.getDeviceId(), AlertMessageKeys.ERROR_DEVICE_ID_REQUIRED);
    validateNotNull(alertDto.getStartTs(), AlertMessageKeys.ERROR_START_TS_REQUIRED);
    validateNotNull(alertDto.getStatus(), AlertMessageKeys.ERROR_STATUS_REQUIRED);

    validateIdMatchesFhirIdRegex(alertDto.getAlertId(),
        AlertMessageKeys.ERROR_ALERT_ID_DOES_NOT_MATCH_REGEX);
    validateInventoryItemExists(alertDto.getDeviceId());
    validateStatusKeysAreLocales(alertDto.getStatus().keySet());
  }

  private void validateNotNull(Object field, String errorMessage) {
    if (null == field) {
      throw new ValidationMessageException(errorMessage);
    }
  }
  
  private void validateIdMatchesFhirIdRegex(String id, String errorMessage) {
    if (!id.matches(FHIR_REGEX)) {
      throw new ValidationMessageException(errorMessage, FHIR_REGEX);
    }
  }
  
  private void validateInventoryItemExists(UUID inventoryItemId) {
    if (!inventoryItemRepository.exists(inventoryItemId)) {
      XLOGGER.warn("Could not validate inventoryItemId = {}, because it was not found.",
          inventoryItemId);
      throw new ValidationMessageException(AlertMessageKeys.ERROR_DEVICE_ID_NOT_FOUND);
    }
  }

  private void validateStatusKeysAreLocales(Set<String> statusKeys) {
    statusKeys.forEach(statusKey -> {
      validateNotNull(statusKey, AlertMessageKeys.ERROR_STATUS_KEY_REQUIRED);
      try {
        new Locale.Builder().setLanguageTag(statusKey).build();
      } catch (IllformedLocaleException ile) {
        XLOGGER.warn("Could not validate statusKey = {}; it is not a valid language tag.",
            statusKey);
      }
    });
  }
}
