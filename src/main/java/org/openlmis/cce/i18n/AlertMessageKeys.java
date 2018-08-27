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

import static org.openlmis.cce.i18n.MessageKeys.REQUIRED;
import static org.openlmis.cce.i18n.MessageKeys.SERVICE_ERROR_PREFIX;
import static org.openlmis.cce.i18n.MessageKeys.join;

public class AlertMessageKeys {

  private static final String ERROR_PREFIX = SERVICE_ERROR_PREFIX + ".alert";

  public static final String ERROR_ALERT_ID_REQUIRED =
      join(ERROR_PREFIX, "alertId", REQUIRED);
  public static final String ERROR_ALERT_ID_DOES_NOT_MATCH_REGEX =
      join(ERROR_PREFIX, "alertId.doesNotMatchRegex");
  public static final String ERROR_ALERT_TYPE_REQUIRED =
      join(ERROR_PREFIX, "alertType", REQUIRED);
  public static final String ERROR_DEVICE_ID_REQUIRED =
      join(ERROR_PREFIX, "deviceId", REQUIRED);
  public static final String ERROR_START_TS_REQUIRED =
      join(ERROR_PREFIX, "startTs", REQUIRED);
  public static final String ERROR_STATUS_REQUIRED =
      join(ERROR_PREFIX, "status", REQUIRED);
  public static final String ERROR_STATUS_KEY_REQUIRED =
      join(ERROR_PREFIX, "statusKey", REQUIRED);
  public static final String ERROR_DEVICE_ID_NOT_FOUND =
      join(ERROR_PREFIX, "deviceId.notFound");

  private AlertMessageKeys() {
    throw new UnsupportedOperationException();
  }
}
