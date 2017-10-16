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

import com.google.common.collect.Maps;

import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.BaseDto;
import org.openlmis.cce.dto.FacilityDto;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.service.BaseCommunicationService;
import org.openlmis.cce.service.referencedata.FacilityReferenceDataService;
import org.openlmis.cce.service.referencedata.UserReferenceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

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
  public List<InventoryItemDto> build(Collection<InventoryItem> inventoryItems) {
    List<FacilityDto> facilities = facilityReferenceDataService.findAll();
    List<UserDto> users = userReferenceDataService.findAll();

    Map<UUID, FacilityDto> facilityCache = Maps.newHashMap();
    Map<UUID, UserDto> userCache = Maps.newHashMap();

    List<InventoryItemDto> built = new ArrayList<>();

    for (InventoryItem item : inventoryItems) {
      if (null == item) {
        continue;
      }

      InventoryItemDto dto = new InventoryItemDto();
      item.export(dto);

      dto.setFacility(getFacility(item.getFacilityId(), facilities, facilityCache));

      if (null != item.getLastModifierId()) {
        dto.setLastModifier(getUser(item.getLastModifierId(), users, userCache));
      }

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

    InventoryItemDto inventoryItemDto = new InventoryItemDto();
    inventoryItem.export(inventoryItemDto);

    inventoryItemDto.setFacility(facilityReferenceDataService
        .findOne(inventoryItem.getFacilityId()));

    if (inventoryItem.getLastModifierId() != null) {
      inventoryItemDto.setLastModifier(userReferenceDataService
          .findOne(inventoryItem.getLastModifierId()));
    }

    return inventoryItemDto;
  }


  private FacilityDto getFacility(UUID facilityId, Collection<FacilityDto> facilities,
                                  Map<UUID, FacilityDto> facilityCache) {
    return get(facilityId, facilities, facilityCache, facilityReferenceDataService);
  }

  private UserDto getUser(UUID userId, Collection<UserDto> users, Map<UUID, UserDto> userCache) {
    return get(userId, users, userCache, userReferenceDataService);
  }

  private <T extends BaseDto> T get(UUID id, Collection<T> collection, Map<UUID, T> cache,
                                    BaseCommunicationService<T> service) {
    return cache.computeIfAbsent(id, new FindElement<>(collection, service));
  }

  @AllArgsConstructor
  private static final class FindElement<T extends BaseDto> implements Function<UUID, T> {
    private Collection<T> collection;
    private BaseCommunicationService<T> service;

    @Override
    public T apply(UUID id) {
      Optional<T> found;

      if (null == collection) {
        found = Optional.empty();
      } else {
        found = collection
            .stream()
            .filter(elem -> Objects.equals(elem.getId(), id))
            .findFirst();
      }

      return found.orElseGet(() -> service.findOne(id));
    }
  }
}
