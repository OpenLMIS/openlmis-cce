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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.InventoryItemDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InventoryItemDtoBuilder {

  @Value("${service.url}")
  private String serviceUrl;

  /**
   * Create a list of {@link InventoryItemDto} based on passed data.
   *
   * @param inventoryItems a list of inventory items that will be converted into DTOs.
   * @return a list of {@link InventoryItemDto}
   */
  public List<InventoryItemDto> build(Collection<InventoryItem> inventoryItems) {

    List<InventoryItemDto> built = new ArrayList<>();

    for (InventoryItem item : inventoryItems) {
      if (null == item) {
        continue;
      }

      InventoryItemDto dto = export(item);

      built.add(dto);
    }

    return built;
  }

  /**
   * Create a new instance of {@link InventoryItemDto} based on data from {@link InventoryItem}.
   *
   * @param inventoryItem instance used to create {@link InventoryItemDto} (can be {@code null})
   * @return new instance of {@link InventoryItemDto}. {@code null} if passed argument is {@code
   * null}.
   */
  public InventoryItemDto build(InventoryItem inventoryItem) {
    if (null == inventoryItem) {
      return null;
    }

    return export(inventoryItem);
  }

  private InventoryItemDto export(InventoryItem item) {
    InventoryItemDto dto = new InventoryItemDto();
    dto.setServiceUrl(serviceUrl);
    item.export(dto);
    return dto;
  }

}
