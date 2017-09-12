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

import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.dto.UploadResultDto;
import org.openlmis.cce.exception.NotFoundException;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.i18n.CatalogItemMessageKeys;
import org.openlmis.cce.i18n.MessageKeys;
import org.openlmis.cce.i18n.MessageService;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.openlmis.cce.service.CatalogItemService;
import org.openlmis.cce.service.PermissionService;
import org.openlmis.cce.util.Message;
import org.openlmis.cce.util.Pagination;
import org.openlmis.cce.web.csv.format.CsvFormatter;
import org.openlmis.cce.web.csv.recordhandler.CatalogItemPersistenceHandler;
import org.openlmis.cce.web.csv.model.ModelClass;
import org.openlmis.cce.web.csv.parser.CsvParser;
import org.openlmis.cce.web.validator.CsvHeaderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_TYPE_NOT_ALLOWED;

@Controller
@Transactional
public class CatalogItemController extends BaseController {

  private static final String DISPOSITION_BASE = "attachment; filename=";
  private static final String RESOURCE_URL = "/catalogItems";
  private static final String TYPE = "type";

  @Autowired
  private MessageService messageService;

  @Autowired
  private CatalogItemRepository catalogRepository;

  @Autowired
  private PermissionService permissionService;

  @Autowired
  private CatalogItemPersistenceHandler catalogItemPersistenceHandler;

  @Autowired
  private CsvParser csvParser;

  @Autowired
  private CsvFormatter csvFormatter;

  @Autowired
  private CsvHeaderValidator csvHeaderValidator;

  @Autowired
  private CatalogItemService catalogItemService;

  /**
   * Allows creating new CCE Catalog Item. If the id is specified, it will be ignored.
   *
   * @param catalogItemDto A CCE catalog item bound to the request body.
   * @return created CCE catalog item.
   */
  @RequestMapping(value = RESOURCE_URL, method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public CatalogItemDto create(@RequestBody CatalogItemDto catalogItemDto) {
    permissionService.canManageCce();
    catalogItemDto.setId(null);
    CatalogItem catalogItem = CatalogItem.newInstance(catalogItemDto);

    CatalogItem newCatalogItem = catalogRepository.save(catalogItem);
    return toDto(newCatalogItem);
  }

  /**
   * Get all CCE Catalog items.
   *
   * @return CCE Catalog items.
   */
  @GetMapping(RESOURCE_URL)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<CatalogItemDto> getAll() {
    permissionService.canManageCce();
    return toDto(catalogRepository.findAll());
  }

  /**
   * Retrieves all CCE Catalog items with specified query params.
   *
   * @param queryParams request parameters: archived, type (optional), visibleInCatalog.
   * @param pageable object used to encapsulate the pagination related values: page and size.
   * @return List of wanted catalog items matching query parameters.
   */
  @RequestMapping(value = RESOURCE_URL + "/search", method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Page<CatalogItemDto> searchCatalogItems(@RequestBody Map<String, Object> queryParams,
                                                 Pageable pageable) {
    permissionService.canManageCce();

    Page<CatalogItem> itemsPage = catalogItemService.search(queryParams, pageable);
    return Pagination.getPage(toDto(itemsPage), pageable, itemsPage.getTotalElements());
  }

  /**
   * Get chosen CCE catalog item.
   *
   * @param catalogItemId UUID of cce catalog item which we want to get
   * @return CCE Catalog Item.
   */
  @RequestMapping(value = RESOURCE_URL + "/{id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public CatalogItemDto getCatalogItem(@PathVariable("id") UUID catalogItemId) {
    permissionService.canManageCce();
    CatalogItem catalogItem = catalogRepository.findOne(catalogItemId);
    if (catalogItem == null) {
      throw new NotFoundException(CatalogItemMessageKeys.ERROR_ITEM_NOT_FOUND);
    } else {
      return toDto(catalogItem);
    }
  }

  /**
   * Updates CCE catalog item.
   *
   * @param catalogItemId  UUID of cce catalog item which we want to update
   * @param catalogItemDto catalog item that will be updated
   * @return updated CCE Catalog Item.
   */
  @RequestMapping(value = RESOURCE_URL + "/{id}", method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public CatalogItemDto updateCatalogItem(@RequestBody CatalogItemDto catalogItemDto,
                                          @PathVariable("id") UUID catalogItemId) {
    permissionService.canManageCce();
    CatalogItem catalogItem = CatalogItem.newInstance(catalogItemDto);
    catalogItem.setId(catalogItemId);
    catalogRepository.save(catalogItem);
    return toDto(catalogItem);
  }

  /**
   * Uploads csv file and converts to domain object.
   *
   * @param file File in ".csv" format to upload.
   * @return number of uploaded records
   */
  @PostMapping(value = RESOURCE_URL, params = TYPE)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public UploadResultDto upload(@RequestParam(TYPE) String type,
                                @RequestPart("file") MultipartFile file) {
    permissionService.canManageCce();

    if (!"csv".equals(type)) {
      throw new NotFoundException(new Message(ERROR_TYPE_NOT_ALLOWED, type));

    }

    validateCsvFile(file);
    ModelClass modelClass = new ModelClass(CatalogItemDto.class);

    try {
      int result = csvParser.process(
          file.getInputStream(), modelClass, catalogItemPersistenceHandler, csvHeaderValidator);
      return new UploadResultDto(result);
    } catch (IOException ex) {
      throw new ValidationMessageException(ex, MessageKeys.ERROR_IO, ex.getMessage());
    }
  }

  /**
   * Downloads csv file with all inventory items.
   */
  @GetMapping(value = RESOURCE_URL, params = TYPE)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public void download(@RequestParam(TYPE) String type,
                       HttpServletResponse response) throws IOException {
    permissionService.canManageCce();

    if (!"csv".equals(type)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          messageService.localize(new Message(ERROR_TYPE_NOT_ALLOWED, type)).asMessage());
      return;
    }

    List<CatalogItemDto> catalogItems = toDto(catalogRepository.findAll());

    response.setContentType("text/csv");
    response.addHeader(HttpHeaders.CONTENT_DISPOSITION,
        DISPOSITION_BASE + "catalog_items.csv");

    try {
      csvFormatter.process(
          response.getOutputStream(), new ModelClass(CatalogItemDto.class), catalogItems);
    } catch (IOException ex) {
      throw new ValidationMessageException(ex, MessageKeys.ERROR_IO, ex.getMessage());
    }
  }


  private CatalogItemDto toDto(CatalogItem catalogItem) {
    CatalogItemDto dto = new CatalogItemDto();
    catalogItem.export(dto);
    return dto;
  }

  private List<CatalogItemDto> toDto(Iterable<CatalogItem> catalogItems) {
    return StreamSupport
        .stream(catalogItems.spliterator(), false)
        .map(this::toDto)
        .collect(Collectors.toList());
  }
}
