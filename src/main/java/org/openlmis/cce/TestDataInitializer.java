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
@Profile("demo-data")
@Order(5)
public class TestDataInitializer implements CommandLineRunner {
  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(TestDataInitializer.class);

  private static final String DEMO_DATA_PATH = "classpath:db/demo-data/";
  private static final String FILE_EXTENSION = ".csv";

  // table names
  private static final String CATALOG_ITEMS = "cce_catalog_items";
  private static final String INVENTORY_ITEMS = "cce_inventory_items";
  private static final String CCE_ALERTS = "cce_alerts";
  private static final String CCE_ALERT_STATUS_MESSAGES = "cce_alert_status_messages";

  // database path
  private static final String DB_SCHEMA = "cce.";
  static final String CATALOG_ITEMS_TABLE = DB_SCHEMA + CATALOG_ITEMS;
  static final String INVENTORY_ITEMS_TABLE = DB_SCHEMA + INVENTORY_ITEMS;
  static final String CCE_ALERTS_TABLE = DB_SCHEMA + CCE_ALERTS;
  static final String CCE_ALERT_STATUS_MESSAGES_TABLE = DB_SCHEMA + CCE_ALERT_STATUS_MESSAGES;


  @Value(value = DEMO_DATA_PATH + DB_SCHEMA + CATALOG_ITEMS + FILE_EXTENSION)
  private Resource catalogItemsResource;

  @Value(value = DEMO_DATA_PATH + DB_SCHEMA + INVENTORY_ITEMS + FILE_EXTENSION)
  private Resource inventoryItemsResource;

  @Value(value = DEMO_DATA_PATH + DB_SCHEMA + CCE_ALERTS + FILE_EXTENSION)
  private Resource cceAlertsResource;

  @Value(value = DEMO_DATA_PATH + DB_SCHEMA + CCE_ALERT_STATUS_MESSAGES + FILE_EXTENSION)
  private Resource cceAlertStatusMessagesResource;

  private Resource2Db loader;

  @Autowired
  public TestDataInitializer(JdbcTemplate template) {
    this(new Resource2Db(template));
  }

  TestDataInitializer(Resource2Db loader) {
    this.loader = loader;
  }

  /**
   * Initializes test data.
   * @param args command line arguments
   */
  public void run(String... args) throws IOException {
    XLOGGER.entry();

    loader.insertToDbFromCsv(CATALOG_ITEMS_TABLE, catalogItemsResource);
    loader.insertToDbFromCsv(INVENTORY_ITEMS_TABLE, inventoryItemsResource);
    loader.insertToDbFromCsv(CCE_ALERTS_TABLE, cceAlertsResource);
    loader.insertToDbFromCsv(CCE_ALERT_STATUS_MESSAGES_TABLE, cceAlertStatusMessagesResource);

    XLOGGER.exit();
  }

}
