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

import static org.openlmis.cce.i18n.VolumeMessageKeys.ERROR_FACILITY_ID_INVALID_UUID_FORMAT;
import static org.openlmis.cce.i18n.VolumeMessageKeys.ERROR_FACILITY_ID_NULL;

import java.util.UUID;
import org.openlmis.cce.exception.ValidationMessageException;
import org.springframework.stereotype.Component;
import org.springframework.validation.ValidationUtils;

@Component
public class VolumeValidator {

  /**
   * Validates the {@code volume} object.
   * The method checks if the object has values in all required properties.
   *
   * @param facilityId the param that will be validated
   * @see ValidationUtils
   */
  public void validate(String facilityId) {
    if (null != facilityId && !facilityId.isEmpty()) {
      validateUuid(facilityId, ERROR_FACILITY_ID_INVALID_UUID_FORMAT);
    } else {
      throw new ValidationMessageException(ERROR_FACILITY_ID_NULL);
    }
  }

  private void validateUuid(String value, String errorMessage) {
    UUID uuidValue = UUID.fromString(value);
    if (!(uuidValue.toString().equals(value))) {
      throw new ValidationMessageException(errorMessage, value);
    }
  }
}
