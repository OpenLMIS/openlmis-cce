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

public class InventoryItemMessageKeys extends MessageKeys {
  private static final String ERROR_PREFIX = SERVICE_ERROR_PREFIX + ".inventory";

  public static final String ERROR_ITEM_NOT_FOUND = ERROR_PREFIX + ".item.notFound";
  public static final String ERROR_CATALOG_ITEM_REQUIRED =
      join(ERROR_PREFIX, "catalogItem", REQUIRED);
  public static final String ERROR_FACILITY_REQUIRED =
      join(ERROR_PREFIX, "facility", REQUIRED);
  public static final String ERROR_PROGRAM_ID_REQUIRED =
      join(ERROR_PREFIX, "programId", REQUIRED);
  public static final String ERROR_YEAR_OF_INSTALLATION_REQUIRED =
      join(ERROR_PREFIX, "yearOfInstallation", REQUIRED);
  public static final String ERROR_FUNCTIONAL_STATUS_REQUIRED =
      join(ERROR_PREFIX, "functionalStatus", REQUIRED);
  public static final String ERROR_UTILIZATION_REQUIRED =
      join(ERROR_PREFIX, "utilization", REQUIRED);
  public static final String ERROR_VOLTAGE_STABILIZER_REQUIRED =
      join(ERROR_PREFIX, "voltageStabilizer", REQUIRED);
  public static final String ERROR_BACKUP_GENERATOR_REQUIRED =
      join(ERROR_PREFIX, "backupGenerator", REQUIRED);
  public static final String ERROR_VOLTAGE_REGULATOR_REQUIRED =
      join(ERROR_PREFIX, "voltageRegulator", REQUIRED);
  public static final String ERROR_MANUAL_TEMPERATURE_GAUGE_REQUIRED =
      join(ERROR_PREFIX, "manualTemperatureGauge", REQUIRED);
  public static final String ERROR_REFERENCE_NAME_REQUIRED =
      join(ERROR_PREFIX, "referenceName", REQUIRED);
  public static final String ERROR_DECOMMISSION_DATE_REQUIRED =
      join(ERROR_PREFIX, "decommissionDate", REQUIRED);
  public static final String ERROR_REMOTE_TEMPERATURE_MONITOR_REQUIRED =
      join(ERROR_PREFIX, "remoteTemperatureMonitor", REQUIRED);

  private InventoryItemMessageKeys() {
    throw new UnsupportedOperationException();
  }
}
