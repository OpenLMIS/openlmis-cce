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

public class VolumeMessageKeys extends MessageKeys {

  private static final String FACILITY_ID = "facilityId";
  private static final String INVALID_UUID_FORMAT = "invalidUuidFormat";
  protected static final String NULL = "null";

  public static final String ERROR_FACILITY_ID_INVALID_UUID_FORMAT =
          join(SERVICE_ERROR_PREFIX, FACILITY_ID, INVALID_UUID_FORMAT);

  public static final String ERROR_FACILITY_ID_NULL =
          join(SERVICE_ERROR_PREFIX, FACILITY_ID, NULL);

}
