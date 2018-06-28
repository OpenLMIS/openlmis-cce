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

import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.i18n.InventoryItemMessageKeys;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.ValidationUtils;

/**
 * A validator for {@link InventoryItemDto} object.
 */
@Component
public class InventoryItemValidator {

  @Autowired
  InventoryItemRepository inventoryItemRepository;

  /**
   * Validates the {@code inventoryItem} object.
   * The method checks if the object has values in all required properties.
   *
   * @param inventoryItem the object that will be validated
   * @see ValidationUtils
   */
  public void validate(InventoryItemDto inventoryItem) {
    validateNotNull(inventoryItem.getCatalogItem(),
        InventoryItemMessageKeys.ERROR_CATALOG_ITEM_REQUIRED);
    validateNotNull(inventoryItem.getFacility(),
        InventoryItemMessageKeys.ERROR_FACILITY_REQUIRED);
    validateNotNull(inventoryItem.getProgramId(),
        InventoryItemMessageKeys.ERROR_PROGRAM_ID_REQUIRED);
    validateNotNull(inventoryItem.getYearOfInstallation(),
        InventoryItemMessageKeys.ERROR_YEAR_OF_INSTALLATION_REQUIRED);
    validateNotNull(inventoryItem.getFunctionalStatus(),
        InventoryItemMessageKeys.ERROR_FUNCTIONAL_STATUS_REQUIRED);
    validateNotNull(inventoryItem.getUtilization(),
        InventoryItemMessageKeys.ERROR_UTILIZATION_REQUIRED);
    validateNotNull(inventoryItem.getVoltageStabilizer(),
        InventoryItemMessageKeys.ERROR_VOLTAGE_STABILIZER_REQUIRED);
    validateNotNull(inventoryItem.getBackupGenerator(),
        InventoryItemMessageKeys.ERROR_BACKUP_GENERATOR_REQUIRED);
    validateNotNull(inventoryItem.getVoltageRegulator(),
        InventoryItemMessageKeys.ERROR_VOLTAGE_REGULATOR_REQUIRED);
    validateNotNull(inventoryItem.getManualTemperatureGauge(),
        InventoryItemMessageKeys.ERROR_MANUAL_TEMPERATURE_GAUGE_REQUIRED);
    validateNotNull(inventoryItem.getReferenceName(),
        InventoryItemMessageKeys.ERROR_REFERENCE_NAME_REQUIRED);
    validateNotNull(inventoryItem.getRemoteTemperatureMonitor(),
        InventoryItemMessageKeys.ERROR_REMOTE_TEMPERATURE_MONITOR_REQUIRED);
    validateUniqueConstraints(inventoryItem);

    if (inventoryItem.getFunctionalStatus().equals(FunctionalStatus.UNSERVICEABLE)) {
      validateNotNull(inventoryItem.getDecommissionDate(),
          InventoryItemMessageKeys.ERROR_DECOMMISSION_DATE_REQUIRED);
    }
    if (inventoryItem.getFunctionalStatus().equals(FunctionalStatus.AWAITING_REPAIR)) {
      validateNotNull(inventoryItem.getReasonNotWorkingOrNotInUse(),
          InventoryItemMessageKeys.ERROR_REASON_REQUIRED);
    }
  }

  private void validateNotNull(Object field, String errorMessage) {
    if (field == null) {
      throw new ValidationMessageException(errorMessage);
    }
  }

  private void validateUniqueConstraints(InventoryItemDto inventoryItem) {
    Boolean exists = inventoryItemRepository
        .existsByEquipmentTrackingIdAndCatalogItem_ModelAndCatalogItem_Type(
            inventoryItem.getEquipmentTrackingId(),
            inventoryItem.getCatalogItem().getModel(),
            inventoryItem.getCatalogItem().getType());
    if (checkIfFieldsAreNull(inventoryItem) && exists) {
      throw new ValidationMessageException(InventoryItemMessageKeys.ERROR_ITEM_ALREADY_EXISTS,
          inventoryItem.getEquipmentTrackingId(),
          inventoryItem.getCatalogItem().getType(),
          inventoryItem.getCatalogItem().getModel());
    }
  }

  private boolean checkIfFieldsAreNull(InventoryItemDto inventoryItem) {
    return inventoryItem.getEquipmentTrackingId() != null
        && inventoryItem.getCatalogItem().getModel() != null
        && inventoryItem.getCatalogItem().getType() != null;
  }
}
