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

import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_NOT_FOUND;

import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.exception.NotFoundException;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.openlmis.cce.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class InventoryItemController extends BaseController {

  @Autowired
  private InventoryItemRepository inventoryRepository;

  @Autowired
  private PermissionService permissionService;

  /**
   * Allows creating new CCE Inventory item. If the id is specified, it will be ignored.
   *
   * @param inventoryItemDto A CCE Inventory item bound to the request body.
   * @return created CCE Inventory item.
   */
  @RequestMapping(value = "/inventoryItems", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public InventoryItemDto create(@RequestBody InventoryItemDto inventoryItemDto) {
    permissionService.canEditInventory();
    inventoryItemDto.setId(null);
    InventoryItem inventoryItem = InventoryItem.newInstance(inventoryItemDto);

    return toDto(inventoryRepository.save(inventoryItem));
  }

  /**
   * Get all CCE Inventory items.
   *
   * @return CCE Inventory items.
   */
  @RequestMapping(value = "/inventoryItems", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<InventoryItemDto> getAll() {
    permissionService.canViewInventory();
    return toDto(inventoryRepository.findAll());
  }

  /**
   * Get chosen CCE Inventory item.
   *
   * @param inventoryItemId UUID of CCE Inventory item which we want to get
   * @return CCE Inventory item.
   */
  @RequestMapping(value = "/inventoryItems/{id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public InventoryItemDto getInventoryItem(@PathVariable("id") UUID inventoryItemId) {
    permissionService.canViewInventory();
    InventoryItem inventoryItem = inventoryRepository.findOne(inventoryItemId);
    if (inventoryItem == null) {
      throw new NotFoundException(ERROR_NOT_FOUND);
    } else {
      return toDto(inventoryItem);
    }
  }

  /**
   * Updates CCE Inventory item.
   *
   * @param inventoryItemId  UUID of CCE Inventory item which we want to update
   * @param inventoryItemDto Inventory item that will be updated
   * @return updated CCE Inventory item.
   */
  @RequestMapping(value = "/inventoryItems/{id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public InventoryItemDto updateInventoryItem(@RequestBody InventoryItemDto inventoryItemDto,
                                            @PathVariable("id") UUID inventoryItemId) {
    permissionService.canEditInventory();
    InventoryItem inventoryItem = InventoryItem.newInstance(inventoryItemDto);
    inventoryItem.setId(inventoryItemId);
    inventoryRepository.save(inventoryItem);
    return toDto(inventoryItem);
  }

  /**
   * Deletes CCE Inventory item with the given id.
   */
  @RequestMapping(value = "/inventoryItems/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteInventoryItem(@PathVariable("id") UUID id) {
    permissionService.canEditInventory();

    InventoryItem inventoryItem = inventoryRepository.findOne(id);
    if (inventoryItem == null) {
      throw new NotFoundException(ERROR_NOT_FOUND);
    }

    inventoryRepository.delete(inventoryItem);
  }

  private InventoryItemDto toDto(InventoryItem inventoryItem) {
    InventoryItemDto dto = new InventoryItemDto();
    inventoryItem.export(dto);
    return dto;
  }

  private List<InventoryItemDto> toDto(Iterable<InventoryItem> inventoryItems) {
    return StreamSupport
        .stream(inventoryItems.spliterator(), false)
        .map(this::toDto)
        .collect(Collectors.toList());
  }
}
