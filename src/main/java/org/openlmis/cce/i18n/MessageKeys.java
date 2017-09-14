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

import java.util.Arrays;

public abstract class MessageKeys {
  private static final String DELIMITER = ".";

  protected static final String SERVICE_PREFIX = "cce";
  static final String SERVICE_ERROR_PREFIX = join(SERVICE_PREFIX, "error");

  protected static final String REQUIRED = "required";
  protected static final String SEARCH = "search";
  protected static final String INVALID = "invalid";
  protected static final String LACKS_PARAMETERS = "lacksParameters";
  protected static final String NOT_UNIQUE = "notUnique";

  public static final String ERROR_IO = SERVICE_ERROR_PREFIX + ".io";
  public static final String ERROR_FILE_IS_EMPTY = SERVICE_ERROR_PREFIX + ".file.empty";
  public static final String ERROR_INCORRECT_FILE_FORMAT = SERVICE_ERROR_PREFIX
      + ".file.format.incorrect";

  protected static String join(String... params) {
    return String.join(DELIMITER, Arrays.asList(params));
  }

  MessageKeys() {
    throw new UnsupportedOperationException();
  }
}
