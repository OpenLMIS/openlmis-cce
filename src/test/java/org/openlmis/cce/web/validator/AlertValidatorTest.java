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

import static org.mockito.Mockito.when;
import static org.openlmis.cce.i18n.AlertMessageKeys.ERROR_ALERT_ID_DOES_NOT_MATCH_REGEX;
import static org.openlmis.cce.i18n.AlertMessageKeys.ERROR_ALERT_ID_REQUIRED;
import static org.openlmis.cce.i18n.AlertMessageKeys.ERROR_ALERT_TYPE_REQUIRED;
import static org.openlmis.cce.i18n.AlertMessageKeys.ERROR_DEVICE_ID_NOT_FOUND;
import static org.openlmis.cce.i18n.AlertMessageKeys.ERROR_DEVICE_ID_REQUIRED;
import static org.openlmis.cce.i18n.AlertMessageKeys.ERROR_START_TS_REQUIRED;
import static org.openlmis.cce.i18n.AlertMessageKeys.ERROR_STATUS_REQUIRED;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.dto.AlertDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.openlmis.cce.util.Message;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public class AlertValidatorTest {

  private static final String ALERT_TYPE_WARNING_HOT = "warning_hot";

  @Rule
  public final ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private InventoryItemRepository inventoryItemRepository;
  
  @InjectMocks
  private AlertValidator alertValidator;

  private AlertDto alertDto;

  @Before
  public void before() {
    alertDto = new AlertDto();
    alertDto.setAlertId(UUID.randomUUID().toString());
    alertDto.setAlertType(ALERT_TYPE_WARNING_HOT);
    alertDto.setDeviceId(UUID.randomUUID());
    alertDto.setStartTs(ZonedDateTime.now());
    alertDto.setStatus(Collections.singletonMap("en_US", "Equipment needs attention: too hot"));

    when(inventoryItemRepository.exists(alertDto.getDeviceId())).thenReturn(true);
  }

  @Test
  public void validateShouldNotThrowExceptionIfRequiredFieldsAreNotNull() {
    alertValidator.validate(alertDto);
  }

  @Test
  public void validateShouldThrowExceptionIfAlertIdIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_ALERT_ID_REQUIRED, "").toString());
    alertDto.setAlertId(null);

    alertValidator.validate(alertDto);
  }

  @Test
  public void validateShouldThrowExceptionIfAlertTypeIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_ALERT_TYPE_REQUIRED, "").toString());
    alertDto.setAlertType(null);

    alertValidator.validate(alertDto);
  }

  @Test
  public void validateShouldThrowExceptionIfDeviceIdIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_DEVICE_ID_REQUIRED, "").toString());
    alertDto.setDeviceId(null);

    alertValidator.validate(alertDto);
  }

  @Test
  public void validateShouldThrowExceptionIfStartTsIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_START_TS_REQUIRED, "").toString());
    alertDto.setStartTs(null);

    alertValidator.validate(alertDto);
  }

  @Test
  public void validateShouldThrowExceptionIfStatusIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_STATUS_REQUIRED, "").toString());
    alertDto.setStatus(null);

    alertValidator.validate(alertDto);
  }

  @Test
  public void validateShouldNotThrowExceptionIfAlertIdMatchesRegex() {
    alertDto.setAlertId("507f1f77bcf86cd799439011");

    alertValidator.validate(alertDto);
  }

  @Test
  public void validateShouldThrowExceptionIfAlertIdDoesNotMatchRegex() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_ALERT_ID_DOES_NOT_MATCH_REGEX, AlertValidator.FHIR_REGEX).toString());
    alertDto.setAlertId("invalidalertid!");

    alertValidator.validate(alertDto);
  }

  @Test
  public void validateShouldNotThrowExceptionIfAlertIdIsInUuidFormat() {
    alertDto.setAlertId("b3c2726e-99f1-462a-980c-43b873586c65");

    alertValidator.validate(alertDto);
  }

  @Test
  public void validateShouldThrowExceptionIfDeviceIdIsNotFound() {
    when(inventoryItemRepository.exists(alertDto.getDeviceId())).thenReturn(false);
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_DEVICE_ID_NOT_FOUND, "").toString());

    alertValidator.validate(alertDto);
  }
}
