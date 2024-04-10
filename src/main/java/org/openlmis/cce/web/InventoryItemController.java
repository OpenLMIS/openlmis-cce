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

import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_FORMAT_NOT_ALLOWED;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_ITEM_NOT_FOUND;
import static org.openlmis.cce.service.ResourceNames.BASE_PATH;
import static org.openlmis.cce.web.InventoryItemController.RESOURCE_PATH;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.dto.InventoryItemTransferDto;
import org.openlmis.cce.exception.NotFoundException;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.i18n.InventoryItemMessageKeys;
import org.openlmis.cce.i18n.MessageKeys;
import org.openlmis.cce.i18n.MessageService;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.openlmis.cce.service.InventoryItemSearchParams;
import org.openlmis.cce.service.InventoryItemService;
import org.openlmis.cce.service.InventoryStatusProcessor;
import org.openlmis.cce.service.ObjReferenceExpander;
import org.openlmis.cce.service.PermissionService;
import org.openlmis.cce.util.AuthenticationHelper;
import org.openlmis.cce.util.Message;
import org.openlmis.cce.util.Pagination;
import org.openlmis.cce.web.validator.InventoryItemValidator;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Transactional
@RequestMapping(RESOURCE_PATH)
public class InventoryItemController extends BaseController {
  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(InventoryItemController.class);

  static final String RESOURCE_PATH = BASE_PATH + "/inventoryItems";
  private static final String PROFILER_CHECK_PERMISSION = "CHECK_PERMISSION";
  private static final String FORMAT = "format";
  private static final String PROGRAM_ID = "programId";
  private static final String FACILITY_ID = "facilityId";
  private static final String CSV = "csv";
  private static final String DISPOSITION_BASE = "attachment; filename=";

  @Autowired
  private InventoryItemRepository inventoryRepository;

  @Autowired
  private InventoryItemService inventoryItemService;

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

  @Autowired
  private ObjReferenceExpander objReferenceExpander;

  @Autowired
  private MessageService messageService;

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
    validator.validate(inventoryItemDto, null);

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
  public InventoryItemDto getInventoryItem(@PathVariable("id") UUID inventoryItemId,
                                           @RequestParam(value = "expand", required = false)
                                               List<String> expands) {
    XLOGGER.entry(inventoryItemId);
    Profiler profiler = new Profiler("GET_INVENTORY_ITEM_BY_ID");
    profiler.setLogger(XLOGGER);

    profiler.start("FIND_IN_DB");
    Optional<InventoryItem> inventoryItem = inventoryRepository.findById(inventoryItemId);
    if (!inventoryItem.isPresent()) {
      profiler.stop().log();
      XLOGGER.exit(inventoryItemId);

      throw new NotFoundException(ERROR_ITEM_NOT_FOUND);
    }

    profiler.start(PROFILER_CHECK_PERMISSION);
    permissionService.canViewInventory(inventoryItem.get());

    profiler.start("PROFILER_CREATE_DTO");
    InventoryItemDto dto = inventoryItemDtoBuilder.build(inventoryItem.get());

    profiler.start("EXPAND_DTO");
    objReferenceExpander.expandDto(dto, expands);

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
  public Page<InventoryItemDto> getAll(InventoryItemSearchParams params,
                                       @SortDefault(sort = "referenceName") Pageable pageable) {
    XLOGGER.entry(params, pageable);
    Profiler profiler = new Profiler("GET_INVENTORY_ITEMS");
    profiler.setLogger(XLOGGER);

    profiler.start("GET_CURRENT_USER");
    UUID userId = authenticationHelper.getCurrentUser().getId();

    profiler.start("SEARCH");
    Page<InventoryItem> itemsPage = inventoryItemService.search(userId, params, pageable);

    profiler.start("CREATE_DTOS");
    List<InventoryItemDto> dtos = inventoryItemDtoBuilder.build(itemsPage.getContent());

    profiler.start("CREATE_PAGE");
    Page<InventoryItemDto> page = Pagination.getPage(dtos, pageable, itemsPage.getTotalElements());

    profiler.start("EXPAND_DTOS");
    expandDtos(page, params);

    profiler.stop().log();
    XLOGGER.exit(page);
    return page;
  }

  /**
   * Transfer CCE Inventory item to specified facility.
   */
  @PutMapping("/{id}/transfer")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void transfer(
          @PathVariable("id") UUID inventoryItemId,
          @RequestBody InventoryItemTransferDto transferDto
  ) {
    InventoryItem inventoryItem = inventoryRepository.findById(inventoryItemId)
            .orElseThrow(() -> new NotFoundException(ERROR_ITEM_NOT_FOUND));

    permissionService.canTransferInventoryItem(
        inventoryItem,
        transferDto.getProgramId(),
        transferDto.getFacilityId()
    );

    InventoryItemDto inventoryItemDto = inventoryItemDtoBuilder.build(inventoryItem);

    inventoryItemDto.setFacilityId(transferDto.getFacilityId());
    inventoryItemDto.setProgramId(transferDto.getProgramId());
    inventoryItemDto.setYearOfInstallation(transferDto.getYearOfInstallation());

    saveInventory(InventoryItem.newInstance(inventoryItemDto));
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

    if (!inventoryItemDto.getId().equals(inventoryItemId)) {
      throw new ValidationMessageException(InventoryItemMessageKeys.ERROR_ID_MISMATCH);
    }

    profiler.start("FIND_IN_DB");
    Optional<InventoryItem> existingInventory = inventoryRepository.findById(inventoryItemId);

    profiler.start(PROFILER_CHECK_PERMISSION);
    if (existingInventory.isPresent()) {
      permissionService.canEditInventory(existingInventory.get());
    } else {
      permissionService.canEditInventory(
          inventoryItemDto.getProgramId(), inventoryItemDto.getFacility().getId());
    }

    profiler.start("VALIDATE");
    existingInventory
        .ifPresent(inventoryItem -> validator.validate(inventoryItemDto, inventoryItem));

    profiler.start("UPDATE_AND_CREATE_DTO");
    InventoryItemDto dto;
    if (existingInventory.isPresent()) {
      dto = updateInventory(inventoryItemDto, existingInventory.get());
    } else {
      InventoryItem inventoryItem = newInventoryItem(inventoryItemDto);

      profiler.start("SAVE_AND_CREATE_DTO");
      dto = saveInventory(inventoryItem);
    }

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
    Optional<InventoryItem> inventoryItem = inventoryRepository.findById(id);
    if (!inventoryItem.isPresent()) {
      profiler.stop().log();
      XLOGGER.exit(id);

      throw new NotFoundException(ERROR_ITEM_NOT_FOUND);
    }

    profiler.start(PROFILER_CHECK_PERMISSION);
    permissionService.canEditInventory(inventoryItem.get());

    profiler.start("DELETE");
    inventoryRepository.delete(inventoryItem.get());

    profiler.stop().log();
    XLOGGER.exit();
  }

  /**
   * Downloads csv file with all inventory items.
   */
  @GetMapping(value = "/download")
  @ResponseStatus(HttpStatus.OK)
  public void download(@RequestParam(FORMAT) String format,
                       @RequestParam(PROGRAM_ID) UUID programId,
                       @RequestParam(FACILITY_ID) UUID facilityId,
                       HttpServletResponse response) throws IOException {
    XLOGGER.entry(format);
    Profiler profiler = new Profiler("DOWNLOAD_INVENTORY_ITEMS_AS_FILE");
    profiler.setLogger(XLOGGER);

    if (!CSV.equals(format)) {
      profiler.stop().log();
      XLOGGER.exit();

      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
              messageService.localize(
                      new Message(ERROR_FORMAT_NOT_ALLOWED, format, CSV)).asMessage());
      return;
    }

    profiler.start("FIND_ALL");
    List<Object[]> items = inventoryRepository.findByFacilityIdAndProgramId(facilityId, programId);

    response.setContentType("text/csv");
    response.addHeader(HttpHeaders.CONTENT_DISPOSITION,
            DISPOSITION_BASE + "inventory_items.csv");

    try {
      profiler.start("WRITE_TO_OUTPUT");
      CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(
              "Facility Name", "Model", "Type", "Net Volume", "Reference Name",
              "Program Name", "Serial No.", "Year of Installation",
              "Year of Warranty Expiry", "Functional Status", "Last Modifier Name",
              "Modified Date"
      );

      CSVPrinter csvPrinter = new CSVPrinter(response.getWriter(), csvFormat);

      for (Object[] item : items) {
        csvPrinter.printRecord(
                item[0], item[1], item[2], item[3], item[4],
                item[5], item[6], item[7], item[8], item[9],
                item[10], item[11]
        );
      }

      csvPrinter.flush();
      csvPrinter.close();
    } catch (IOException ex) {
      throw new ValidationMessageException(
              ex, MessageKeys.ERROR_IO, ex.getMessage());
    } finally {
      profiler.stop().log();
      XLOGGER.exit();
    }
  }

  private InventoryItemDto updateInventory(InventoryItemDto inventoryItemDto,
                                           InventoryItem existingInventory) {
    InventoryItem inventoryItem = newInventoryItem(inventoryItemDto);
    boolean changed = inventoryItem.statusChanged(existingInventory);
    existingInventory.updateFrom(inventoryItem);
    InventoryItemDto itemDto = saveInventory(existingInventory);
    if (changed) {
      inventoryStatusProcessor.functionalStatusChange(itemDto);
    }
    return itemDto;
  }

  private InventoryItem newInventoryItem(InventoryItemDto inventoryItemDto) {
    inventoryItemDto.setLastModifierId(authenticationHelper.getCurrentUser().getId());
    return InventoryItem.newInstance(inventoryItemDto);
  }

  private InventoryItemDto saveInventory(InventoryItem inventoryItem) {
    inventoryItem.setModifiedDate(ZonedDateTime.now());
    return inventoryItemDtoBuilder.build(inventoryRepository.save(inventoryItem));
  }

  private void expandDtos(Page<InventoryItemDto> page,
                          InventoryItemSearchParams params) {
    for (InventoryItemDto dto : page) {
      objReferenceExpander.expandDto(dto, params.getExpand());
    }
  }
}
