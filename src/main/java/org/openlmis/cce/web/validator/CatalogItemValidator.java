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

import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_ARCHIVED_REQUIRED;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_ENERGY_SOURCE_REQUIRED;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_EQUIPMENT_CODE_NOT_UNIQUE;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_FROM_PSQ_CATALOG_REQUIRED;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_MANUFACTURER_MODEL_NOT_UNIQUE;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_MANUFACTURER_REQUIRED;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_MODEL_REQUIRED;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_STORAGE_TEMPERATURE_REQUIRED;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_TYPE_REQUIRED;

import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CatalogItemValidator {

  @Autowired
  CatalogItemRepository catalogItemRepository;

  /**
   * Validate catalog item that will be added.
   */
  public void validateNewCatalogItem(CatalogItemDto catalogItem) {
    validateNullValues(catalogItem);
    validateUniqueConstraints(catalogItem);
  }

  /**
   * Validate already existing catalog item.
   */
  public void validateExistingCatalogItem(CatalogItemDto catalogItem) {
    validateNullValues(catalogItem);
  }

  private void validateUniqueConstraints(CatalogItemDto catalogItem) {
    if (catalogItem.getEquipmentCode() != null
        && catalogItemRepository.existsByEquipmentCode(catalogItem.getEquipmentCode())) {
      throw new ValidationMessageException(ERROR_EQUIPMENT_CODE_NOT_UNIQUE,
          catalogItem.getEquipmentCode());
    }

    if (catalogItemRepository.existsByManufacturerAndModel(catalogItem.getManufacturer(),
        catalogItem.getModel())) {
      throw new ValidationMessageException(ERROR_MANUFACTURER_MODEL_NOT_UNIQUE,
          catalogItem.getManufacturer(), catalogItem.getModel());
    }
  }

  private void validateNullValues(CatalogItemDto catalogItem) {
    validateNotNull(catalogItem.getFromPqsCatalog(), ERROR_FROM_PSQ_CATALOG_REQUIRED);
    validateNotNull(catalogItem.getType(), ERROR_TYPE_REQUIRED);
    validateNotNull(catalogItem.getModel(), ERROR_MODEL_REQUIRED);
    validateNotNull(catalogItem.getManufacturer(), ERROR_MANUFACTURER_REQUIRED);
    validateNotNull(catalogItem.getEnergySource(), ERROR_ENERGY_SOURCE_REQUIRED);
    validateNotNull(catalogItem.getStorageTemperature(), ERROR_STORAGE_TEMPERATURE_REQUIRED);
    validateNotNull(catalogItem.getArchived(), ERROR_ARCHIVED_REQUIRED);
  }

  private void validateNotNull(Object field, String errorMessage) {
    if (field == null) {
      throw new ValidationMessageException(errorMessage);
    }
  }
}
