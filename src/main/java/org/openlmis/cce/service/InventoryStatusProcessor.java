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

package org.openlmis.cce.service;

import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.service.notifier.NonfunctionalCceNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InventoryStatusProcessor {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private NonfunctionalCceNotifier nonfunctionalCceNotifier;

  /**
   * Process inventory functional status change.
   * @param inventoryItem a inventoryItem that has just changed its status
   */
  public void functionalStatusChange(InventoryItem inventoryItem) {
    logger.debug("Status processor called with: " + inventoryItem);
    if (inventoryItem.getFunctionalStatus() == FunctionalStatus.NON_FUNCTIONING) {
      nonfunctionalCceNotifier.notify(inventoryItem);
    }
  }
}
