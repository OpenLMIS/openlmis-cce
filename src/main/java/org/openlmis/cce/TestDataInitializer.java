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

package org.openlmis.cce;

import org.openlmis.cce.util.Resource2Db;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile("performance-data")
@Order(5)
public class TestDataInitializer implements CommandLineRunner {
  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(TestDataInitializer.class);
  private static final String PERF_DATA_PATH = "classpath:db/performance-data/";

  @Value(value = PERF_DATA_PATH + "catalog_items.csv")
  private Resource catalogItemsResource;

  @Value(value = PERF_DATA_PATH + "inventory_items.csv")
  private Resource inventoryItemsResource;

  @Value(value = PERF_DATA_PATH + "cce_alerts.csv")
  private Resource cceAlertsResource;

  @Value(value = PERF_DATA_PATH + "cce_alert_status_messages.csv")
  private Resource cceAlertStatusMessagesResource;

  @Autowired
  private JdbcTemplate template;

  /**
   * Initializes test data.
   * @param args command line arguments
   */
  public void run(String... args) throws IOException {
    XLOGGER.entry();

    Resource2Db r2db = new Resource2Db(template);

    r2db.insertToDbFromCsv("cce.cce_catalog_items", catalogItemsResource);
    r2db.insertToDbFromCsv("cce.cce_inventory_items", inventoryItemsResource);
    r2db.insertToDbFromCsv("cce.cce_alerts", cceAlertsResource);
    r2db.insertToDbFromCsv("cce.cce_alert_status_messages", cceAlertStatusMessagesResource);

    XLOGGER.exit();
  }
}