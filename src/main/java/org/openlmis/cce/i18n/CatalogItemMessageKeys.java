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

package org.openlmis.cce.i18n;

public class CatalogItemMessageKeys extends MessageKeys {
  private static final String ERROR_PREFIX = SERVICE_ERROR_PREFIX + ".catalog";

  public static final String ERROR_ITEM_NOT_FOUND = ERROR_PREFIX + ".item.notFound";
  public static final String ERROR_FORMAT_NOT_ALLOWED =
      join(ERROR_PREFIX, ".format.notAllowed");

  public static final String ERROR_FROM_FIELD_REQUIRED =
      join(ERROR_PREFIX, "field", REQUIRED);

  public static final String ERROR_EQUIPMENT_CODE_NOT_UNIQUE =
      join(ERROR_PREFIX, "equipmentCode", NOT_UNIQUE);
  public static final String ERROR_MANUFACTURER_MODEL_NOT_UNIQUE =
      join(ERROR_PREFIX, "manufacturerAndModel", NOT_UNIQUE);

  private CatalogItemMessageKeys() {
    throw new UnsupportedOperationException();
  }
}
