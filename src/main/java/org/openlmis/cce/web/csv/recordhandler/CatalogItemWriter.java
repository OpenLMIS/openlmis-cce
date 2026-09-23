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

import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_AMBIGUOUS_MATCH;
import static org.openlmis.cce.i18n.CatalogItemMessageKeys.ERROR_DUPLICATE_IN_FILE;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
      Map<Pair<String, String>, UUID> groupByEquipmentCodeAndModel = Maps.newHashMap();
      Map<Pair<String, String>, UUID> groupByManufacturerAndModel = Maps.newHashMap();

      for (CatalogItem item : existing) {
        if (null != item.getEquipmentCode()) {
          groupByEquipmentCodeAndModel.put(
              ImmutablePair.of(item.getEquipmentCode(), item.getModel()), item.getId());
        }

        groupByManufacturerAndModel.put(
            ImmutablePair.of(item.getManufacturer(), item.getModel()), item.getId());
      }

      profiler.start("FIND_IN_GROUPS");
      assignExistingIds(entities, groupByEquipmentCodeAndModel, groupByManufacturerAndModel);
    }

    profiler.start("SAVE");
    catalogItemRepository.saveAll(entities);

    profiler.stop().log();
    XLOGGER.exit();
  }

  private void assignExistingIds(List<CatalogItem> entities,
                                 Map<Pair<String, String>, UUID> groupByEquipmentCodeAndModel,
                                 Map<Pair<String, String>, UUID> groupByManufacturerAndModel) {
    Map<UUID, CatalogItem> claimed = Maps.newHashMap();

    for (CatalogItem item : entities) {
      UUID existingId = resolveExistingId(
          item, groupByEquipmentCodeAndModel, groupByManufacturerAndModel);

      if (null == existingId) {
        continue;
      }

      if (null != claimed.put(existingId, item)) {
        throw new ValidationMessageException(
            ERROR_DUPLICATE_IN_FILE, item.getManufacturer(), item.getModel());
      }

      item.setId(existingId);
    }
  }

  private UUID resolveExistingId(CatalogItem item,
                                 Map<Pair<String, String>, UUID> groupByEquipmentCodeAndModel,
                                 Map<Pair<String, String>, UUID> groupByManufacturerAndModel) {
    UUID byEquipmentCode = null == item.getEquipmentCode()
        ? null
        : groupByEquipmentCodeAndModel.get(
            ImmutablePair.of(item.getEquipmentCode(), item.getModel()));

    UUID byManufacturer = groupByManufacturerAndModel.get(
        ImmutablePair.of(item.getManufacturer(), item.getModel()));

    if (null != byEquipmentCode && null != byManufacturer
        && !byEquipmentCode.equals(byManufacturer)) {
      throw new ValidationMessageException(ERROR_AMBIGUOUS_MATCH,
          item.getEquipmentCode(), item.getManufacturer(), item.getModel());
    }

    return null != byEquipmentCode ? byEquipmentCode : byManufacturer;
  }

}
