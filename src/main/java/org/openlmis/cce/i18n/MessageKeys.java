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

public abstract class MessageKeys {
  private static final String SERVICE_PREFIX = "cce";
  private static final String ERROR_PREFIX = SERVICE_PREFIX + ".error";

  public static final String ERROR_USER_NOT_FOUND = ERROR_PREFIX
      + ".authentication.userCanNotBeFound";
  public static final String ERROR_RIGHT_NOT_FOUND = ERROR_PREFIX
      + ".authentication.rightCanNotBeFound";
  public static final String ERROR_NO_FOLLOWING_PERMISSION = ERROR_PREFIX
      + ".authentication.noFollowingPermission";
  public static final String ERROR_UPLOD_INVALID_HEADER = ERROR_PREFIX
      + ".upload.invalid.header";
  public static final String ERROR_UPLOAD_MISSING_MANDATORY_COLUMNS = ERROR_PREFIX
      + ".upload.missing.mandatory.columns";
  public static final String ERROR_UPLOAD_HEADER_MISSING = ERROR_PREFIX
      + ".upload.header.missing";
  public static final String ERROR_IO = ERROR_PREFIX + ".io";
  public static final String ERROR_FILE_IS_EMPTY = ERROR_PREFIX + ".fileIsEmpty";
  public static final String ERROR_INCORRECT_FILE_FORMAT = ERROR_PREFIX + ".incorrectFileFormat";

  private MessageKeys() {
    throw new UnsupportedOperationException();
  }
}
