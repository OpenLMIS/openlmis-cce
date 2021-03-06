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

public class PermissionMessageKeys extends MessageKeys {

  private static final String ERROR_PREFIX = SERVICE_ERROR_PREFIX + ".authentication";

  public static final String ERROR_USER_NOT_FOUND = ERROR_PREFIX + ".user.notFound";
  public static final String ERROR_RIGHT_NOT_FOUND = ERROR_PREFIX + ".right.notFound";
  public static final String ERROR_NO_FOLLOWING_PERMISSION = ERROR_PREFIX
      + ".noFollowingPermission";
  public static final String ERROR_API_KEYS_ONLY = ERROR_PREFIX
      + ".apiKeysOnly";

  private PermissionMessageKeys() {
    throw new UnsupportedOperationException();
  }
}
