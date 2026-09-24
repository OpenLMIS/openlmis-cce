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

package org.openlmis.cce.errorhandling;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.hibernate.exception.ConstraintViolationException;
import org.openlmis.cce.exception.AuthenticationMessageException;
import org.openlmis.cce.exception.NotFoundException;
import org.openlmis.cce.exception.PermissionMessageException;
import org.openlmis.cce.exception.ServerException;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.i18n.CatalogItemMessageKeys;
import org.openlmis.cce.i18n.MessageKeys;
import org.openlmis.cce.service.DataRetrievalException;
import org.openlmis.cce.util.Message;
import org.openlmis.util.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Global error handling for all controllers in the service.
 * Contains common error handling mappings.
 */
@ControllerAdvice
public class GlobalErrorHandling extends AbstractErrorHandling {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandling.class);

  private static final Map<String, String> CONSTRAINT_MAP = ImmutableMap.of(
      "unq_catalog_items_man_model", CatalogItemMessageKeys.ERROR_MANUFACTURER_AND_MODEL_DUPLICATE,
      "unq_catalog_items_eqcode", CatalogItemMessageKeys.ERROR_EQUIPMENT_CODE_AND_MODEL_DUPLICATE
  );

  /**
   * Turns a database constraint violation into a message the user can act on. The raw statement
   * goes to the log; it must never reach the response, because the service resolves an unknown
   * key to the key itself and would echo the SQL back verbatim.
   *
   * @param dive the exception thrown when a write breaks a constraint
   * @return the localized message
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Message.LocalizedMessage handleDataIntegrityViolation(
      DataIntegrityViolationException dive) {
    if (dive.getCause() instanceof ConstraintViolationException) {
      String constraintName = ((ConstraintViolationException) dive.getCause()).getConstraintName();
      String messageKey = CONSTRAINT_MAP.get(constraintName);

      if (null != messageKey) {
        LOGGER.info("Constraint {} violated", constraintName);
        return getLocalizedMessage(new Message(messageKey));
      }
    }

    LOGGER.error("Unmapped data integrity violation", dive);
    return getLocalizedMessage(new Message(MessageKeys.ERROR_DATA_INTEGRITY_VIOLATION));
  }
  
  @ExceptionHandler(DataRetrievalException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorResponse handleRefDataException(DataRetrievalException ex) {
    return logErrorAndRespond("Error fetching from reference data", ex);
  }

  @ExceptionHandler(ServerException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorResponse handleServerException(ServerException ex) {
    return logErrorAndRespond("An internal error occurred", ex);
  }

  @ExceptionHandler(AuthenticationMessageException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ResponseBody
  public Message.LocalizedMessage handleAuthenticationException(AuthenticationMessageException ex) {
    return getLocalizedMessage(ex);
  }

  @ExceptionHandler(PermissionMessageException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ResponseBody
  public Message.LocalizedMessage handlePermissionException(PermissionMessageException ex) {
    return getLocalizedMessage(ex);
  }

  @ExceptionHandler(ValidationMessageException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public Message.LocalizedMessage handleValidationMessageException(ValidationMessageException ex) {
    return getLocalizedMessage(ex);
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public Message.LocalizedMessage handleNotFoundException(NotFoundException ex) {
    return getLocalizedMessage(ex);
  }
}
