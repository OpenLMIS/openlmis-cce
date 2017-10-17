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

package org.openlmis.cce.web.csv.recordhandler;

import static org.springframework.util.CollectionUtils.isEmpty;

import com.google.common.collect.Maps;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is responsible for saving {@link CatalogItem} instances to the database.
 */
@Component
public class CatalogItemWriter implements RecordWriter<CatalogItem> {
  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(CatalogItemWriter.class);

  @Autowired
  private CatalogItemRepository catalogItemRepository;

  @Override
  public void write(List<CatalogItem> entities) {
    XLOGGER.entry();
    Profiler profiler = new Profiler("WRITE");
    profiler.setLogger(XLOGGER);

    profiler.start("FIND_EXISTING");
    List<CatalogItem> existing = catalogItemRepository.findExisting(entities);

    if (!isEmpty(existing)) {
      profiler.start("CREATE_GROUPS");
      Map<String, UUID> groupByEquipmentCode = Maps.newHashMap();
      Map<Pair<String, String>, UUID> groupByManufacturerAndModel = Maps.newHashMap();

      for (CatalogItem item : existing) {
        UUID id = item.getId();
        String equipmentCode = item.getEquipmentCode();

        if (null != equipmentCode) {
          groupByEquipmentCode.put(equipmentCode, id);
        }

        String manufacturer = item.getManufacturer();
        String model = item.getModel();
        Pair<String, String> key = ImmutablePair.of(manufacturer, model);

        groupByManufacturerAndModel.put(key, id);
      }

      profiler.start("FIND_IN_GROUPS");
      for (int i = 0, size = entities.size(); i < size; ++i) {
        CatalogItem item = entities.get(i);
        String equipmentCode = item.getEquipmentCode();
        UUID existingId;

        if (null != equipmentCode) {
          existingId = groupByEquipmentCode.get(equipmentCode);
        } else {
          ImmutablePair<String, String> key = ImmutablePair.of(
              item.getManufacturer(), item.getModel()
          );
          existingId = groupByManufacturerAndModel.get(key);
        }

        if (null != existingId) {
          item.setId(existingId);
        }
      }
    }

    profiler.start("SAVE");
    catalogItemRepository.save(entities);

    profiler.stop().log();
    XLOGGER.exit();
  }

}
