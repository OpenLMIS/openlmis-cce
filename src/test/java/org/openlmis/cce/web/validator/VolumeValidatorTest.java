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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.util.Message;

public class VolumeValidatorTest {

  @Rule
  public final ExpectedException expectedEx = ExpectedException.none();

  @InjectMocks
  private VolumeValidator volumeValidator = new VolumeValidator();

  private String facilityId;

  @Test
  public void shouldNotThrowExceptionIfRequiredFieldsAreNotNull() {
    facilityId = "8f90f904-3c3c-11ea-a232-2e728ce88125";
    volumeValidator.validate(facilityId);
  }

  @Test
  public void shouldThrowExceptionIfFacilityIdIsIncorrect() {
    facilityId = "7f7b83db-580d-4269-88d2-7f9c80a";
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
            new Message(ERROR_FACILITY_ID_INVALID_UUID_FORMAT, facilityId).toString());
    volumeValidator.validate(facilityId);
  }

  @Test
  public void shouldThrowExceptionIfFacilityIdIsNull() {
    facilityId = null;
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
            new Message(ERROR_FACILITY_ID_NULL, facilityId).toString());
    volumeValidator.validate(facilityId);
  }
}
