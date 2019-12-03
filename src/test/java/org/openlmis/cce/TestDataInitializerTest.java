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

import static org.mockito.Mockito.verify;
import static org.openlmis.cce.TestDataInitializer.CATALOG_ITEMS_TABLE;
import static org.openlmis.cce.TestDataInitializer.CCE_ALERTS_TABLE;
import static org.openlmis.cce.TestDataInitializer.CCE_ALERT_STATUS_MESSAGES_TABLE;
import static org.openlmis.cce.TestDataInitializer.INVENTORY_ITEMS_TABLE;

import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openlmis.cce.util.Resource2Db;
import org.springframework.core.io.Resource;

@RunWith(MockitoJUnitRunner.class)
public class TestDataInitializerTest {

  @Mock
  private Resource catalogItemsResource;

  @Mock
  private Resource inventoryItemsResource;

  @Mock
  private Resource cceAlertsResource;

  @Mock
  private Resource cceAlertStatusMessagesResource;

  @Mock
  private Resource2Db loader;

  @InjectMocks
  private TestDataInitializer initializer = new TestDataInitializer(loader);

  @Test
  public void shouldLoadData() throws IOException {
    initializer.run();

    verify(loader).insertToDbFromCsv(CATALOG_ITEMS_TABLE, catalogItemsResource);
    verify(loader).insertToDbFromCsv(INVENTORY_ITEMS_TABLE, inventoryItemsResource);
    verify(loader).insertToDbFromCsv(CCE_ALERTS_TABLE, cceAlertsResource);
    verify(loader)
        .insertToDbFromCsv(CCE_ALERT_STATUS_MESSAGES_TABLE, cceAlertStatusMessagesResource);
  }
}
