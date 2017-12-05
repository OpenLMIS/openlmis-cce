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

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_VIEW;

import com.google.common.collect.Sets;

import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.PermissionStringDto;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class InventoryItemService {

  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(InventoryItemService.class);

  @Autowired
  private InventoryItemRepository repository;

  @Autowired
  private PermissionService permissionService;

  /**
   * This method is supposed to retrieve all inventory items with matched parameters. Result is
   * sorted and paginated by pageable parameter. You can use 'type' sort value and it will sort
   * inventory items by their catalog item type.
   *
   * @param   userId    the id of the user to search the inventoryItems for
   * @param   params    the search parameters
   * @param   pageable  the pagination and sort parameters
   * @return            the page of inventory items matching given parameters
   */
  public Page<InventoryItem> search(UUID userId, InventoryItemSearchParams params,
                                    Pageable pageable) {

    XLOGGER.entry(userId, params, pageable);
    Profiler profiler = new Profiler("INVENTORY_ITEM_SERVICE_SEARCH");
    profiler.setLogger(XLOGGER);

    profiler.start("GET_PERMISSION_STRINGS");
    PermissionStrings.Handler handler = permissionService.getPermissionStrings(userId);
    Set<PermissionStringDto> permissionStrings = handler.get();

    profiler.start("GET_PROGRAMS_AND_FACILITIES");
    Set<UUID> programIds = Sets.newHashSet();
    Set<UUID> facilityIds = Sets.newHashSet();

    UUID facilityId = params.getFacilityId();
    for (PermissionStringDto permissionString : permissionStrings) {
      if (equalsIgnoreCase(CCE_INVENTORY_VIEW, permissionString.getRightName())) {
        if (facilityId == null || permissionString.getFacilityId().equals(facilityId)) {
          facilityIds.add(permissionString.getFacilityId());
        }
        programIds.add(permissionString.getProgramId());
      }
    }

    profiler.start("INVENTORY_ITEM_REPOSITORY_SEARCH");
    Page<InventoryItem> page = repository.search(
        facilityIds,
        programIds,
        params.getFunctionalStatus(),
        pageable
    );

    profiler.stop().log();
    XLOGGER.exit(page);
    return page;
  }

}
