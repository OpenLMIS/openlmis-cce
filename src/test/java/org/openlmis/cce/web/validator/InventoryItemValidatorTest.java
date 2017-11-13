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

import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_BACKUP_GENERATOR_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_CATALOG_ITEM_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_DECOMMISSION_DATE_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_FACILITY_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_FUNCTIONAL_STATUS_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_MANUAL_TEMPERATURE_GAUGE_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_PROGRAM_ID_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_REASON_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_REFERENCE_NAME_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_REMOTE_TEMPERATURE_MONITOR_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_UTILIZATION_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_VOLTAGE_REGULATOR_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_VOLTAGE_STABILIZER_REQUIRED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_YEAR_OF_INSTALLATION_REQUIRED;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.domain.BackupGeneratorStatus;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.ManualTemperatureGaugeType;
import org.openlmis.cce.domain.ReasonNotWorkingOrNotInUse;
import org.openlmis.cce.domain.RemoteTemperatureMonitorType;
import org.openlmis.cce.domain.Utilization;
import org.openlmis.cce.domain.VoltageRegulatorStatus;
import org.openlmis.cce.domain.VoltageStabilizerStatus;
import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.dto.ObjectReferenceDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.util.Message;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"PMD.TooManyMethods"})
public class InventoryItemValidatorTest {

  @Rule
  public final ExpectedException expectedEx = ExpectedException.none();

  @InjectMocks
  private InventoryItemValidator inventoryItemValidator;

  private InventoryItemDto inventoryItemDto;

  private CatalogItemDto catalogItemDto;

  private ObjectReferenceDto facility;

  @Before
  public void before() {
    catalogItemDto = new CatalogItemDto();
    catalogItemDto.setId(UUID.randomUUID());

    facility = new ObjectReferenceDto(UUID.randomUUID(), "facilities");

    inventoryItemDto = new InventoryItemDto("localhost", facility, catalogItemDto,
        UUID.randomUUID(), "eqTrackingId", "Some Reference Name", 2010, 2020,
        "some source", FunctionalStatus.FUNCTIONING,
        ReasonNotWorkingOrNotInUse.NOT_APPLICABLE, Utilization.ACTIVE,
        VoltageStabilizerStatus.UNKNOWN, BackupGeneratorStatus.YES, VoltageRegulatorStatus.NO,
        ManualTemperatureGaugeType.BUILD_IN, RemoteTemperatureMonitorType.BUILD_IN, "someMonitorId",
        "example notes", null, null, null);
  }

  @Test
  public void shouldNotThrowExceptionIfRequiredFieldsAreNotNull() {
    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfCatalogItemIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_CATALOG_ITEM_REQUIRED, "").toString());

    inventoryItemDto.setCatalogItem(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfFacilityIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_FACILITY_REQUIRED, "").toString());

    inventoryItemDto.setFacility(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfProgramIdIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_PROGRAM_ID_REQUIRED, "").toString());

    inventoryItemDto.setProgramId(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfYearOfInstallationIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_YEAR_OF_INSTALLATION_REQUIRED, "").toString());

    inventoryItemDto.setYearOfInstallation(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfFunctionalStatusIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_FUNCTIONAL_STATUS_REQUIRED, "").toString());

    inventoryItemDto.setFunctionalStatus(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfUtilizationIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_UTILIZATION_REQUIRED, "").toString());

    inventoryItemDto.setUtilization(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfVoltageStabilizerIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_VOLTAGE_STABILIZER_REQUIRED, "").toString());

    inventoryItemDto.setVoltageStabilizer(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfBackupGeneratorIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_BACKUP_GENERATOR_REQUIRED, "").toString());

    inventoryItemDto.setBackupGenerator(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfVoltageRegulatorIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_VOLTAGE_REGULATOR_REQUIRED, "").toString());

    inventoryItemDto.setVoltageRegulator(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfManualTemperatureGaugeIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_MANUAL_TEMPERATURE_GAUGE_REQUIRED, "").toString());

    inventoryItemDto.setManualTemperatureGauge(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfReferenceNameIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_REFERENCE_NAME_REQUIRED, "").toString());

    inventoryItemDto.setReferenceName(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfStatusIsObsoleteAndDecommissionDateIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_DECOMMISSION_DATE_REQUIRED, "").toString());

    inventoryItemDto.setFunctionalStatus(FunctionalStatus.OBSOLETE);
    inventoryItemDto.setDecommissionDate(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfStatusIsNonFunctioningAndReasonIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_REASON_REQUIRED, "").toString());

    inventoryItemDto.setFunctionalStatus(FunctionalStatus.NON_FUNCTIONING);
    inventoryItemDto.setReasonNotWorkingOrNotInUse(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

  @Test
  public void shouldThrowExceptionIfRemoteTemperatureMonitorTypeIsNull() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_REMOTE_TEMPERATURE_MONITOR_REQUIRED, "").toString());

    inventoryItemDto.setRemoteTemperatureMonitor(null);

    inventoryItemValidator.validate(inventoryItemDto);
  }

}
