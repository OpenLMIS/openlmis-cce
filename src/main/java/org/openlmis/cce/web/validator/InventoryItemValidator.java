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

import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.i18n.InventoryItemMessageKeys;
import org.springframework.stereotype.Component;
import org.springframework.validation.ValidationUtils;

/**
 * A validator for {@link InventoryItemDto} object.
 */
@Component
public class InventoryItemValidator {

  /**
   * Validates the {@code inventoryItem} object.
   * The method checks if the object has a value in {@code catalogItem} property.
   *
   * @param inventoryItem the object that will be validated
   * @see ValidationUtils
   */
  public void validate(InventoryItemDto inventoryItem) {
    if (inventoryItem.getCatalogItem() == null) {
      throw new ValidationMessageException(InventoryItemMessageKeys.ERROR_CATALOG_ITEM_REQUIRED);
    }
  }
}
