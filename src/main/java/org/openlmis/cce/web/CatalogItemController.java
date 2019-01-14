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

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.servlet.http.HttpServletResponse;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.dto.UploadResultDto;
import org.openlmis.cce.exception.NotFoundException;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.i18n.CatalogItemMessageKeys;
import org.openlmis.cce.i18n.MessageKeys;
import org.openlmis.cce.i18n.MessageService;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.openlmis.cce.service.PermissionService;
import org.openlmis.cce.util.Message;
import org.openlmis.cce.util.Pagination;
import org.openlmis.cce.web.csv.format.CsvFormatter;
import org.openlmis.cce.web.csv.model.ModelClass;
import org.openlmis.cce.web.csv.parser.CsvParser;
import org.openlmis.cce.web.csv.recordhandler.CatalogItemProcessor;
import org.openlmis.cce.web.csv.recordhandler.CatalogItemWriter;
import org.openlmis.cce.web.validator.CatalogItemValidator;
import org.openlmis.cce.web.validator.CsvHeaderValidator;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
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

@Controller
@Transactional
public class CatalogItemController extends BaseController {
  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(CatalogItemController.class);

  private static final String DISPOSITION_BASE = "attachment; filename=";
  private static final String RESOURCE_URL = "/catalogItems";
  private static final String FORMAT = "format";
  private static final String CSV = "csv";

  private static final String PROFILER_CREATE_DTO = "PROFILER_CREATE_DTO";

  @Autowired
  private MessageService messageService;

  @Autowired
  private CatalogItemRepository catalogRepository;

  @Autowired
  private PermissionService permissionService;

  @Autowired
  private CatalogItemProcessor catalogItemProcessor;

  @Autowired
  private CatalogItemWriter catalogItemWriter;

  @Autowired
  private CsvParser csvParser;

  @Autowired
  private CsvFormatter csvFormatter;

  @Autowired
  private CsvHeaderValidator csvHeaderValidator;

  @Autowired
  private CatalogItemValidator catalogItemValidator;

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
    XLOGGER.entry(catalogItemDto);
    Profiler profiler = new Profiler("CREATE_CATALOG_ITEM");
    profiler.setLogger(XLOGGER);

    profiler.start("CHECK_PERMISSION");
    permissionService.canManageCce();

    profiler.start("VALIDATE");
    catalogItemValidator.validateNewCatalogItem(catalogItemDto);

    profiler.start("CREATE_DOMAIN_INSTANCE");
    catalogItemDto.setId(null);
    CatalogItem catalogItem = CatalogItem.newInstance(catalogItemDto);

    profiler.start("SAVE");
    CatalogItem newCatalogItem = catalogRepository.save(catalogItem);

    profiler.start(PROFILER_CREATE_DTO);
    CatalogItemDto dto = toDto(newCatalogItem);

    profiler.stop().log();
    XLOGGER.exit(dto);

    return dto;
  }

  /**
   * Retrieves all CCE Catalog items with specified request params.
   *
   * @param type             request parameter: type
   * @param archived         request parameter: archived
   * @param visibleInCatalog request parameter: visibleInCatalog
   * @param pageable         object used to encapsulate the pagination values: page and size.
   * @return Page of wanted catalog items matching request parameters.
   */
  @GetMapping(value = RESOURCE_URL)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Page<CatalogItemDto> search(
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "archived", required = false) Boolean archived,
      @RequestParam(value = "visibleInCatalog", required = false) Boolean visibleInCatalog,
      Pageable pageable) {
    XLOGGER.entry(type, archived, visibleInCatalog, pageable);
    Profiler profiler = new Profiler("SEARCH_CATALOG_ITEMS");
    profiler.setLogger(XLOGGER);

    profiler.start("SEARCH_IN_DB");
    Page<CatalogItem> itemsPage = catalogRepository.search(type, archived,
        visibleInCatalog, pageable);

    profiler.start(PROFILER_CREATE_DTO);
    List<CatalogItemDto> dtos = toDto(itemsPage.getContent());

    profiler.start("CREATE_PAGE");
    Page<CatalogItemDto> page = Pagination.getPage(dtos, pageable, itemsPage.getTotalElements());

    profiler.stop().log();
    XLOGGER.exit(page);

    return page;
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
    XLOGGER.entry(catalogItemId);
    Profiler profiler = new Profiler("GET_CATALOG_ITEM_BY_ID");
    profiler.setLogger(XLOGGER);

    profiler.start("SEARCH_IN_DB");
    CatalogItem catalogItem = catalogRepository.findOne(catalogItemId);
    if (catalogItem == null) {
      profiler.stop().log();
      XLOGGER.exit();

      throw new NotFoundException(CatalogItemMessageKeys.ERROR_ITEM_NOT_FOUND);
    } else {
      profiler.start(PROFILER_CREATE_DTO);
      CatalogItemDto dto = toDto(catalogItem);

      profiler.stop().log();
      XLOGGER.exit(dto);

      return dto;
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
    XLOGGER.entry(catalogItemId);
    Profiler profiler = new Profiler("UPDATE_CATALOG_ITEM");
    profiler.setLogger(XLOGGER);

    profiler.start("CHECK_PERMISSION");
    permissionService.canManageCce();

    profiler.start("VALIDATE");

    catalogItemValidator.validateMisMatchCatalogItem(catalogItemDto,catalogItemId);

    profiler.start("CREATE_DOMAIN_INSTANCE");
    CatalogItem catalogItem = CatalogItem.newInstance(catalogItemDto);
    catalogItem.setId(catalogItemId);

    profiler.start("SAVE");
    catalogRepository.save(catalogItem);

    profiler.start(PROFILER_CREATE_DTO);
    CatalogItemDto dto = toDto(catalogItem);

    profiler.stop().log();
    XLOGGER.exit(dto);

    return dto;
  }

  /**
   * Uploads csv file and converts to domain object.
   *
   * @param file File in ".csv" format to upload.
   * @return number of uploaded records
   */
  @PostMapping(value = RESOURCE_URL, params = FORMAT)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public UploadResultDto upload(@RequestParam(FORMAT) String format,
                                @RequestPart("file") MultipartFile file) {
    XLOGGER.entry(format);
    Profiler profiler = new Profiler("UPLOAD_CATALOG_ITEMS_FILE");
    profiler.setLogger(XLOGGER);

    profiler.start("CHECK_PERMISSION");
    permissionService.canManageCce();

    if (!CSV.equals(format)) {
      profiler.stop().log();
      XLOGGER.exit();

      throw new NotFoundException(new Message(ERROR_FORMAT_NOT_ALLOWED, format, CSV));
    }

    profiler.start("VALIDATE");
    validateCsvFile(file);

    profiler.start("CREATE_MODEL_CLASS");
    ModelClass<CatalogItemDto> modelClass = new ModelClass<>(CatalogItemDto.class);

    try {

      profiler.start("PARSE_FILE");
      int result = csvParser.parse(
          file.getInputStream(), modelClass, csvHeaderValidator,
          catalogItemProcessor, catalogItemWriter
      );

      profiler.start("CREATE_RESPONSE");
      UploadResultDto uploadResult = new UploadResultDto(result);

      profiler.stop().log();
      XLOGGER.exit(uploadResult);

      return uploadResult;
    } catch (IOException ex) {
      profiler.stop().log();
      XLOGGER.exit();

      throw new ValidationMessageException(ex, MessageKeys.ERROR_IO, ex.getMessage());
    }
  }

  /**
   * Downloads csv file with all catalog items.
   */
  @GetMapping(value = RESOURCE_URL, params = FORMAT)
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public void download(@RequestParam(FORMAT) String format,
                       HttpServletResponse response) throws IOException {
    XLOGGER.entry(format);
    Profiler profiler = new Profiler("DOWNLOAD_CATALOG_ITEMS_AS_FILE");
    profiler.setLogger(XLOGGER);

    if (!CSV.equals(format)) {
      profiler.stop().log();
      XLOGGER.exit();

      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          messageService.localize(new Message(ERROR_FORMAT_NOT_ALLOWED, format, CSV)).asMessage());
      return;
    }

    profiler.start("FIND_ALL");
    Iterable<CatalogItem> items = catalogRepository.findAll();

    profiler.start(PROFILER_CREATE_DTO);
    List<CatalogItemDto> catalogItems = toDto(items);

    response.setContentType("text/csv");
    response.addHeader(HttpHeaders.CONTENT_DISPOSITION,
        DISPOSITION_BASE + "catalog_items.csv");

    try {
      profiler.start("WRITE_TO_OUTPUT");
      csvFormatter.process(
          response.getOutputStream(), new ModelClass<>(CatalogItemDto.class), catalogItems);
    } catch (IOException ex) {
      throw new ValidationMessageException(ex, MessageKeys.ERROR_IO, ex.getMessage());
    } finally {
      profiler.stop().log();
      XLOGGER.exit();
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
