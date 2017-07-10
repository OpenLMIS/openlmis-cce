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
import org.openlmis.cce.exception.NotFoundException;
import org.openlmis.cce.i18n.CatalogItemMessageKeys;
import org.openlmis.cce.repository.CatalogItemRepository;
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
public class CatalogItemController extends BaseController {

  @Autowired
  private CatalogItemRepository catalogRepository;

  @Autowired
  private PermissionService permissionService;

  /**
   * Allows creating new CCE Catalog Item. If the id is specified, it will be ignored.
   *
   * @param catalogItemDto A CCE catalog item bound to the request body.
   * @return created CCE catalog item.
   */
  @RequestMapping(value = "/catalogItems", method = RequestMethod.POST)
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
  @RequestMapping(value = "/catalogItems", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<CatalogItemDto> getAll() {
    permissionService.canManageCce();
    return toDto(catalogRepository.findAll());
  }

  /**
   * Get chosen CCE catalog item.
   *
   * @param catalogItemId UUID of cce catalog item which we want to get
   * @return CCE Catalog Item.
   */
  @RequestMapping(value = "/catalogItems/{id}", method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public CatalogItemDto getCatalogItem(@PathVariable("id") UUID catalogItemId) {
    permissionService.canManageCce();
    CatalogItem catalogItem = catalogRepository.findOne(catalogItemId);
    if (catalogItem == null) {
      throw new NotFoundException(CatalogItemMessageKeys.ERROR_NOT_FOUND);
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
  @RequestMapping(value = "/catalogItems/{id}", method = RequestMethod.PUT)
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
