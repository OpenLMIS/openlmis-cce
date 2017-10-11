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

import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.FacilityDto;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.service.referencedata.FacilityReferenceDataService;
import org.openlmis.cce.service.referencedata.UserReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class InventoryItemDtoBuilder {

  @Autowired
  private FacilityReferenceDataService facilityReferenceDataService;

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  /**
   * Create a list of {@link InventoryItemDto} based on passed data.
   *
   * @param inventoryItems a list of inventory items that will be converted into DTOs.
   * @return a list of {@link InventoryItemDto}
   */
  public List<InventoryItemDto> build(Collection<InventoryItem> inventoryItems,
                                      Collection<FacilityDto> facilities) {
    return inventoryItems
        .stream()
        .map(item -> build(item, facilities))
        .collect(Collectors.toList());
  }

  public InventoryItemDto build(InventoryItem inventoryItem) {
    return build(inventoryItem, null);
  }

  /**
   * Create a new instance of {@link InventoryItemDto} based on data from {@link InventoryItem}.
   *
   * @param inventoryItem instance used to create {@link InventoryItemDto} (can be {@code null})
   * @return new instance of {@link InventoryItemDto}. {@code null} if passed argument is {@code
   * null}.
   */
  public InventoryItemDto build(InventoryItem inventoryItem, Collection<FacilityDto> facilities) {
    if (null == inventoryItem) {
      return null;
    }

    InventoryItemDto inventoryItemDto = new InventoryItemDto();
    inventoryItem.export(inventoryItemDto);

    inventoryItemDto.setFacility(getFacility(inventoryItem.getFacilityId(), facilities));

    if (inventoryItem.getLastModifierId() != null) {
      inventoryItemDto.setLastModifier(userReferenceDataService
          .findOne(inventoryItem.getLastModifierId()));
    }

    return inventoryItemDto;
  }

  private FacilityDto getFacility(UUID facilityId, Collection<FacilityDto> facilities) {
    Optional<FacilityDto> found;

    if (null == facilities) {
      found = Optional.empty();
    } else {
      found = facilities
          .stream()
          .filter(facility -> Objects.equals(facility.getId(), facilityId))
          .findFirst();
    }

    return found.orElseGet(() -> facilityReferenceDataService.findOne(facilityId));
  }
}
