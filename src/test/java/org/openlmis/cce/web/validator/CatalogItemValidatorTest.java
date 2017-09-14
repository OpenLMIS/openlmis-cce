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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.domain.Dimensions;
import org.openlmis.cce.domain.EnergySource;
import org.openlmis.cce.domain.StorageTemperature;
import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.openlmis.cce.util.Message;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_ARCHIVED_REQUIRED;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_ENERGY_SOURCE_REQUIRED;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_EQUIPMENT_CODE_NOT_UNIQUE;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_FROM_PSQ_CATALOG_REQUIRED;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_MANUFACTURER_MODEL_NOT_UNIQUE;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_MANUFACTURER_REQUIRED;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_MODEL_REQUIRED;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_STORAGE_TEMPERATURE_REQUIRED;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_TYPE_REQUIRED;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"PMD.TooManyMethods"})
public class CatalogItemValidatorTest {

  @Rule
  public final ExpectedException expectedEx = ExpectedException.none();

  @Mock
  private CatalogItemRepository catalogItemRepository;

  @InjectMocks
  private CatalogItemValidator catalogItemValidator;

  private CatalogItemDto catalogItemDto;

  @Before
  public void before() {
    initMocks(this);
    catalogItemDto = new CatalogItemDto(true, "equipment-code",
        "type", "model", "producent", EnergySource.ELECTRIC, 2016,
        StorageTemperature.MINUS3, 20, -20, "LOW", 1, 1, 1,
        new Dimensions(100, 100, 100), true, false);
  }

  @Test
  public void shouldNotThrowExceptionIfRequiredFieldsAreNotNull() {
    catalogItemValidator.validateExistingCatalogItem(catalogItemDto);
    catalogItemValidator.validateNewCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfFromPqsCatalogIsNullForExistingItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_FROM_PSQ_CATALOG_REQUIRED, "").toString());

    catalogItemDto.setFromPqsCatalog(null);
    catalogItemValidator.validateExistingCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfFromPqsCatalogIsNullForNewItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_FROM_PSQ_CATALOG_REQUIRED, "").toString());

    catalogItemDto.setFromPqsCatalog(null);
    catalogItemValidator.validateNewCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfTypeIsNullForExistingItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_TYPE_REQUIRED, "").toString());

    catalogItemDto.setType(null);
    catalogItemValidator.validateExistingCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfTypeIsNullForNewItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_TYPE_REQUIRED, "").toString());

    catalogItemDto.setType(null);
    catalogItemValidator.validateNewCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfModelIsNullForExistingItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_MODEL_REQUIRED, "").toString());

    catalogItemDto.setModel(null);
    catalogItemValidator.validateExistingCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfModelIsNullForNewItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_MODEL_REQUIRED, "").toString());

    catalogItemDto.setModel(null);
    catalogItemValidator.validateNewCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfManufacturerIsNullForExistingItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_MANUFACTURER_REQUIRED, "").toString());

    catalogItemDto.setManufacturer(null);
    catalogItemValidator.validateExistingCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfManufacturerIsNullForNewItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_MANUFACTURER_REQUIRED, "").toString());

    catalogItemDto.setManufacturer(null);
    catalogItemValidator.validateNewCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfEnergySourceIsNullForExistingItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_ENERGY_SOURCE_REQUIRED, "").toString());

    catalogItemDto.setEnergySource(null);
    catalogItemValidator.validateExistingCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfEnergySourceIsNullForNewItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_ENERGY_SOURCE_REQUIRED, "").toString());

    catalogItemDto.setEnergySource(null);
    catalogItemValidator.validateNewCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfStorageTemperatureIsNullForExistingItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_STORAGE_TEMPERATURE_REQUIRED, "").toString());

    catalogItemDto.setStorageTemperature(null);
    catalogItemValidator.validateExistingCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfStorageTemperatureIsNullForNewItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_STORAGE_TEMPERATURE_REQUIRED, "").toString());

    catalogItemDto.setStorageTemperature(null);
    catalogItemValidator.validateNewCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfArchivedIsNullForExistingItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_ARCHIVED_REQUIRED, "").toString());

    catalogItemDto.setArchived(null);
    catalogItemValidator.validateExistingCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfArchivedIsNullForNewItem() {
    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_ARCHIVED_REQUIRED, "").toString());

    catalogItemDto.setArchived(null);
    catalogItemValidator.validateNewCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfItemWithGivenEquipmentCodeExists() {
    when(catalogItemRepository.existsByEquipmentCode(catalogItemDto.getEquipmentCode()))
        .thenReturn(true);

    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_EQUIPMENT_CODE_NOT_UNIQUE,
            catalogItemDto.getEquipmentCode()).toString());

    catalogItemValidator.validateNewCatalogItem(catalogItemDto);
  }

  @Test
  public void shouldThrowExceptionIfItemWithGivenModelAndManufacturerExists() {
    when(catalogItemRepository.existsByManufacturerAndModel(catalogItemDto.getManufacturer(),
        catalogItemDto.getModel())).thenReturn(true);

    expectedEx.expect(ValidationMessageException.class);
    expectedEx.expectMessage(
        new Message(ERROR_MANUFACTURER_MODEL_NOT_UNIQUE, catalogItemDto.getManufacturer(),
            catalogItemDto.getModel()).toString());

    catalogItemValidator.validateNewCatalogItem(catalogItemDto);
  }
}
