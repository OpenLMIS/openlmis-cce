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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openlmis.cce.i18n.CatalogItemMessageKeys;
import org.openlmis.cce.i18n.MessageKeys;
import org.openlmis.cce.i18n.MessageService;
import org.openlmis.cce.util.Message;
import org.springframework.dao.DataIntegrityViolationException;

@RunWith(MockitoJUnitRunner.class)
public class GlobalErrorHandlingTest {

  private static final String RAW_SQL = "could not execute statement; SQL [n/a]; "
      + "constraint [unq_catalog_items_man_model]";

  @Mock
  private MessageService messageService;

  @Captor
  private ArgumentCaptor<Message> messageCaptor;

  @InjectMocks
  private GlobalErrorHandling globalErrorHandling;

  @Test
  public void shouldMapTheManufacturerAndModelConstraint() {
    handle("unq_catalog_items_man_model");

    assertThat(captured(), equalTo(
        new Message(CatalogItemMessageKeys.ERROR_MANUFACTURER_AND_MODEL_DUPLICATE)));
  }

  @Test
  public void shouldMapTheEquipmentCodeAndModelConstraint() {
    handle("unq_catalog_items_eqcode");

    assertThat(captured(), equalTo(
        new Message(CatalogItemMessageKeys.ERROR_EQUIPMENT_CODE_AND_MODEL_DUPLICATE)));
  }

  @Test
  public void shouldFallBackToAGenericMessageForAnUnmappedConstraint() {
    handle("some_constraint_nobody_mapped");

    assertThat(captured(), equalTo(new Message(MessageKeys.ERROR_DATA_INTEGRITY_VIOLATION)));
  }

  @Test
  public void shouldNeverPutTheRawStatementInTheResponse() {
    handle("some_constraint_nobody_mapped");

    assertThat(captured().toString(), not(containsString("SQL [n/a]")));
    assertThat(captured().toString(), not(containsString("could not execute statement")));
    assertThat(captured().toString(), containsString("cce.error."));
  }

  @Test
  public void shouldNotThrowWhenTheExceptionCarriesNoCause() {
    globalErrorHandling.handleDataIntegrityViolation(new DataIntegrityViolationException(RAW_SQL));

    assertThat(captured(), equalTo(new Message(MessageKeys.ERROR_DATA_INTEGRITY_VIOLATION)));
  }

  @Test
  public void shouldFallBackWhenTheCauseIsNotAConstraintViolation() {
    globalErrorHandling.handleDataIntegrityViolation(
        new DataIntegrityViolationException(RAW_SQL, new IllegalStateException("boom")));

    assertThat(captured(), equalTo(new Message(MessageKeys.ERROR_DATA_INTEGRITY_VIOLATION)));
  }

  private void handle(String constraintName) {
    ConstraintViolationException cause = new ConstraintViolationException(
        RAW_SQL, new java.sql.SQLException("duplicate key"), constraintName);

    globalErrorHandling.handleDataIntegrityViolation(
        new DataIntegrityViolationException(RAW_SQL, cause));
  }

  private Message captured() {
    verify(messageService).localize(messageCaptor.capture());
    return messageCaptor.getValue();
  }
}
