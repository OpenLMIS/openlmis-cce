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

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.i18n.CatalogItemMessageKeys;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class CatalogItemService {

  @Autowired
  private CatalogItemRepository catalogRepository;

  /**
   * Method returns all catalog items with matched parameters.
   *
   * @param queryMap request parameters (archived, type, visibleInCatalog).
   * @return List of facilities
   */
  public Page<CatalogItem> search(Map<String, Object> queryMap, Pageable pageable) {

    if (MapUtils.isEmpty(queryMap)) {
      return catalogRepository.findAll(pageable);
    }

    Boolean archived = MapUtils.getBoolean(queryMap, "archived", null);
    Boolean visibleInCatalog = MapUtils.getBoolean(queryMap, "visibleInCatalog", null);
    String type = MapUtils.getString(queryMap, "type", null);

    if (archived == null
        && visibleInCatalog == null
        && StringUtils.isEmpty(type)) {
      throw new ValidationMessageException(
          CatalogItemMessageKeys.ERROR_SEARCH_LACKS_PARAMS);
    }

    return catalogRepository.findByArchivedAndTypeAndVisibleInCatalog(
        archived, type, visibleInCatalog, pageable);
  }

}
