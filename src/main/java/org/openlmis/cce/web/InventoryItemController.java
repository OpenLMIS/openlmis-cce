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

package org.openlmis.cce.web;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_ITEM_NOT_FOUND;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_VIEW;
import static org.openlmis.cce.service.ResourceNames.BASE_PATH;
import static org.openlmis.cce.web.InventoryItemController.RESOURCE_PATH;

import com.google.common.collect.Sets;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.domain.User;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.dto.PermissionStringDto;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.exception.NotFoundException;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.openlmis.cce.service.InventoryStatusProcessor;
import org.openlmis.cce.service.PermissionService;
import org.openlmis.cce.service.PermissionStrings;
import org.openlmis.cce.util.AuthenticationHelper;
import org.openlmis.cce.util.Pagination;
import org.openlmis.cce.web.validator.InventoryItemValidator;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
@Transactional
@RequestMapping(RESOURCE_PATH)
public class InventoryItemController extends BaseController {
  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(InventoryItemController.class);

  static final String RESOURCE_PATH = BASE_PATH + "/inventoryItems";
  private static final String PROFILER_CHECK_PERMISSION = "CHECK_PERMISSION";

  @Autowired
  private InventoryItemRepository inventoryRepository;

  @Autowired
  private PermissionService permissionService;

  @Autowired
  private AuthenticationHelper authenticationHelper;

  @Autowired
  private InventoryItemDtoBuilder inventoryItemDtoBuilder;

  @Autowired
  private InventoryItemValidator validator;

  @Autowired
  private InventoryStatusProcessor inventoryStatusProcessor;

  /**
   * Allows creating new CCE Inventory item. If the id is specified, it will be ignored.
   *
   * @param inventoryItemDto A CCE Inventory item bound to the request body.
   * @return created CCE Inventory item.
   */
  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public InventoryItemDto create(@RequestBody InventoryItemDto inventoryItemDto) {
    XLOGGER.entry(inventoryItemDto);
    Profiler profiler = new Profiler("CREATE_INVENTORY_ITEM");
    profiler.setLogger(XLOGGER);

    profiler.start(PROFILER_CHECK_PERMISSION);
    permissionService.canEditInventory(
        inventoryItemDto.getProgramId(), inventoryItemDto.getFacility().getId());

    profiler.start("VALIDATE");
    validator.validate(inventoryItemDto);

    profiler.start("CREATE_DOMAIN_INSTANCE");
    inventoryItemDto.setId(null);
    InventoryItem inventoryItem = newInventoryItem(inventoryItemDto);

    profiler.start("SAVE_AND_CREATE_DTO");
    InventoryItemDto dto = saveInventory(inventoryItem);

    profiler.stop().log();
    XLOGGER.exit(dto);
    return dto;
  }

  /**
   * Get chosen CCE Inventory item.
   *
   * @param inventoryItemId UUID of CCE Inventory item which we want to get
   * @return CCE Inventory item.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public InventoryItemDto getInventoryItem(@PathVariable("id") UUID inventoryItemId) {
    XLOGGER.entry(inventoryItemId);
    Profiler profiler = new Profiler("GET_INVENTORY_ITEM_BY_ID");
    profiler.setLogger(XLOGGER);

    profiler.start("FIND_IN_DB");
    InventoryItem inventoryItem = inventoryRepository.findOne(inventoryItemId);
    if (inventoryItem == null) {
      profiler.stop().log();
      XLOGGER.exit(inventoryItemId);

      throw new NotFoundException(ERROR_ITEM_NOT_FOUND);
    }

    profiler.start(PROFILER_CHECK_PERMISSION);
    permissionService.canViewInventory(inventoryItem);

    profiler.start("PROFILER_CREATE_DTO");
    InventoryItemDto dto = inventoryItemDtoBuilder.build(inventoryItem);

    profiler.stop().log();
    XLOGGER.exit(dto);
    return dto;
  }

  /**
   * Get all CCE Inventory items that user has right for.
   *
   * @return CCE Inventory items.
   */
  @RequestMapping(method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Page<InventoryItemDto> getAll(Pageable pageable) {
    XLOGGER.entry(pageable);
    Profiler profiler = new Profiler("GET_INVENTORY_ITEMS");
    profiler.setLogger(XLOGGER);

    profiler.start("GET_CURRENT_USER");
    UUID userId = authenticationHelper.getCurrentUser().getId();

    profiler.start("GET_PERMISSION_STRINGS");
    PermissionStrings.Handler handler = permissionService.getPermissionStrings(userId);
    Set<PermissionStringDto> permissionStrings = handler.get();

    profiler.start("GET_PROGRAMS_AND_FACILITIES");
    Set<UUID> programIds = Sets.newHashSet();
    Set<UUID> facilityIds = Sets.newHashSet();

    for (PermissionStringDto permissionString : permissionStrings) {
      if (equalsIgnoreCase(CCE_INVENTORY_VIEW, permissionString.getRightName())) {
        facilityIds.add(permissionString.getFacilityId());
        programIds.add(permissionString.getProgramId());
      }
    }

    profiler.start("SEARCH");
    Page<InventoryItem> itemsPage = inventoryRepository.search(facilityIds, programIds, pageable);

    profiler.start("CREATE_DTOS");
    List<InventoryItemDto> dtos = inventoryItemDtoBuilder.build(itemsPage.getContent());

    profiler.start("CREATE_PAGE");
    Page<InventoryItemDto> page = Pagination.getPage(dtos, pageable, itemsPage.getTotalElements());

    profiler.stop().log();
    XLOGGER.exit(page);
    return page;
  }

  /**
   * Updates CCE Inventory item.
   *
   * @param inventoryItemId  UUID of CCE Inventory item which we want to update
   * @param inventoryItemDto Inventory item that will be updated
   * @return updated CCE Inventory item.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public InventoryItemDto updateInventoryItem(@RequestBody InventoryItemDto inventoryItemDto,
                                              @PathVariable("id") UUID inventoryItemId) {
    XLOGGER.entry(inventoryItemId);
    Profiler profiler = new Profiler("UPDATE_INVENTORY_ITEM");
    profiler.setLogger(XLOGGER);

    profiler.start("FIND_IN_DB");
    InventoryItem existingInventory = inventoryRepository.findOne(inventoryItemId);

    profiler.start(PROFILER_CHECK_PERMISSION);
    if (existingInventory != null) {
      permissionService.canEditInventory(existingInventory);
    } else {
      permissionService.canEditInventory(
          inventoryItemDto.getProgramId(), inventoryItemDto.getFacility().getId());
    }

    profiler.start("VALIDATE");
    validator.validate(inventoryItemDto);

    profiler.start("UPDATE_AND_CREATE_DTO");
    InventoryItemDto dto = updateInventory(inventoryItemDto, inventoryItemId, existingInventory);

    profiler.stop().log();
    XLOGGER.exit(dto);
    return dto;
  }

  /**
   * Deletes CCE Inventory item with the given id.
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteInventoryItem(@PathVariable("id") UUID id) {
    XLOGGER.entry(id);
    Profiler profiler = new Profiler("DELETE_INVENTORY_ITEM");
    profiler.setLogger(XLOGGER);

    profiler.start("FIND_IN_DB");
    InventoryItem inventoryItem = inventoryRepository.findOne(id);
    if (inventoryItem == null) {
      profiler.stop().log();
      XLOGGER.exit(id);

      throw new NotFoundException(ERROR_ITEM_NOT_FOUND);
    }

    profiler.start(PROFILER_CHECK_PERMISSION);
    permissionService.canEditInventory(inventoryItem);

    profiler.start("DELETE");
    inventoryRepository.delete(inventoryItem);

    profiler.stop().log();
    XLOGGER.exit();
  }

  private InventoryItemDto updateInventory(InventoryItemDto inventoryItemDto,
                                           UUID inventoryItemId,
                                           InventoryItem existingInventory) {
    InventoryItem inventoryItem = newInventoryItem(inventoryItemDto);
    boolean changed = inventoryItem.statusChanged(existingInventory);
    existingInventory.updateFrom(inventoryItem);
    InventoryItemDto itemDto = saveInventory(inventoryItem);
    if (changed) {
      inventoryStatusProcessor.functionalStatusChange(itemDto);
    }
    return itemDto;
  }

  private InventoryItem newInventoryItem(InventoryItemDto inventoryItemDto) {
    UserDto currentUser = authenticationHelper.getCurrentUser();
    return InventoryItem.newInstance(inventoryItemDto,
        new User(currentUser.getId(), currentUser.getFirstName(), currentUser.getLastName()));
  }

  private InventoryItemDto saveInventory(InventoryItem inventoryItem) {
    inventoryItem.setModifiedDate(ZonedDateTime.now());
    return inventoryItemDtoBuilder.build(inventoryRepository.save(inventoryItem));
  }
}
