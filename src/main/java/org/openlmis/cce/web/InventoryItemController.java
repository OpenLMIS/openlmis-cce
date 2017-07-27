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

import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_ITEM_NOT_FOUND;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_VIEW;

import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.FacilityDto;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.dto.ProgramDto;
import org.openlmis.cce.exception.NotFoundException;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.openlmis.cce.service.PermissionService;
import org.openlmis.cce.service.referencedata.UserSupervisedFacilitiesReferenceDataService;
import org.openlmis.cce.service.referencedata.UserSupervisedProgramsReferenceDataService;
import org.openlmis.cce.util.AuthenticationHelper;
import org.openlmis.cce.util.Pagination;
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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@Transactional
public class InventoryItemController extends BaseController {

  @Autowired
  private InventoryItemRepository inventoryRepository;

  @Autowired
  private UserSupervisedProgramsReferenceDataService supervisedProgramsReferenceDataService;

  @Autowired
  private UserSupervisedFacilitiesReferenceDataService supervisedFacilitiesReferenceDataService;

  @Autowired
  private PermissionService permissionService;

  @Autowired
  private AuthenticationHelper authenticationHelper;

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
    permissionService.canEditInventory(
        inventoryItemDto.getProgramId(), inventoryItemDto.getFacilityId());
    inventoryItemDto.setId(null);
    InventoryItem inventoryItem = newInventoryItem(inventoryItemDto);

    return saveInventory(inventoryItem);
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
    InventoryItem inventoryItem = inventoryRepository.findOne(inventoryItemId);
    if (inventoryItem == null) {
      throw new NotFoundException(ERROR_ITEM_NOT_FOUND);
    }

    permissionService.canViewInventory(inventoryItem);

    return toDto(inventoryItem);
  }

  /**
   * Get all CCE Inventory items that user has right for.
   *
   * @return CCE Inventory items.
   */
  @RequestMapping(value = "/inventoryItems", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Page<InventoryItemDto> getAll(Pageable pageable) {
    UUID userId = authenticationHelper.getCurrentUser().getId();
    UUID rightId = authenticationHelper.getRight(CCE_INVENTORY_VIEW).getId();
    Collection<ProgramDto> programs = supervisedProgramsReferenceDataService
        .getProgramsSupervisedByUser(userId);

    Set<FacilityDto> facilities = new LinkedHashSet<>();
    for (ProgramDto program : programs) {
      facilities.addAll(supervisedFacilitiesReferenceDataService
          .getFacilitiesSupervisedByUser(userId, program.getId(), rightId));
    }

    Set<InventoryItem> inventoryItems = new LinkedHashSet<>();
    for (ProgramDto program : programs) {
      for (FacilityDto facility : facilities) {
        inventoryItems.addAll(
            inventoryRepository.findByFacilityIdAndProgramId(facility.getId(), program.getId()));
      }
    }
    return Pagination.getPage(toDto(inventoryItems), pageable);
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
    InventoryItem existingInventory = inventoryRepository.findOne(inventoryItemId);
    if (existingInventory != null) {
      permissionService.canEditInventory(existingInventory);
    } else {
      permissionService.canEditInventory(
          inventoryItemDto.getProgramId(), inventoryItemDto.getFacilityId());
    }

    InventoryItem inventoryItem = newInventoryItem(inventoryItemDto);
    inventoryItem.setId(inventoryItemId);
    inventoryItem.setInvariants(existingInventory);
    return saveInventory(inventoryItem);
  }

  /**
   * Deletes CCE Inventory item with the given id.
   */
  @RequestMapping(value = "/inventoryItems/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteInventoryItem(@PathVariable("id") UUID id) {
    InventoryItem inventoryItem = inventoryRepository.findOne(id);
    if (inventoryItem == null) {
      throw new NotFoundException(ERROR_ITEM_NOT_FOUND);
    }

    permissionService.canEditInventory(inventoryItem);

    inventoryRepository.delete(inventoryItem);
  }

  private InventoryItem newInventoryItem(InventoryItemDto inventoryItemDto) {
    return InventoryItem.newInstance(inventoryItemDto,
        authenticationHelper.getCurrentUser().getId());
  }

  private InventoryItemDto saveInventory(InventoryItem inventoryItem) {
    inventoryItem.setModifiedDate(ZonedDateTime.now());
    return toDto(inventoryRepository.save(inventoryItem));
  }

  private InventoryItemDto toDto(InventoryItem inventoryItem) {
    InventoryItemDto dto = new InventoryItemDto();
    inventoryItem.export(dto);
    return dto;
  }

  private List<InventoryItemDto> toDto(Collection<InventoryItem> inventoryItems) {
    return inventoryItems
        .stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }
}
