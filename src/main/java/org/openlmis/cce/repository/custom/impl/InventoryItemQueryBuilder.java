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

package org.openlmis.cce.repository.custom.impl;

import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.openlmis.cce.domain.FunctionalStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@AllArgsConstructor
class InventoryItemQueryBuilder {
  static final String SELECT_SQL = "SELECT i FROM InventoryItem AS i"
      + " INNER JOIN FETCH i.catalogItem AS c";
  static final String COUNT_SQL = "SELECT count(i) FROM InventoryItem AS i";

  static final String FACILITY_PREDICATE = "i.facilityId IN ('%s')";
  static final String PROGRAM_PREDICATE = "i.programId IN ('%s')";
  static final String STATUS_PREDICATE = "i.functionalStatus = '%s'";

  private static final String WHERE = "WHERE";
  private static final String ORDER_BY = "ORDER BY";

  private static final String AND = "AND";
  private static final String ASC = "ASC";
  private static final String DESC = "DESC";

  private static final String COLLECTION_JOINER = "','";
  private static final String COMMA = ",";
  private static final char SPACE = ' ';

  private static final String TYPE = "type";
  private static final String C_TYPE = "c.type";

  private final Collection<UUID> facilityIds;
  private final Collection<UUID> programIds;
  private final FunctionalStatus functionalStatus;
  private final Pageable pageable;
  private final boolean count;

  String build() {
    List<String> query = Lists.newArrayList();

    addSelect(query);
    addWhere(query);
    addOrder(query, pageable);

    return Joiner.on(SPACE).join(query);
  }

  private void addSelect(List<String> query) {
    if (count) {
      query.add(COUNT_SQL);
    } else {
      query.add(SELECT_SQL);
    }
  }

  private void addWhere(List<String> query) {
    boolean hasFacilities = !isEmpty(facilityIds);
    boolean hasPrograms = !isEmpty(programIds);
    boolean hasFunctionalStatus = functionalStatus != null;

    if (hasFacilities || hasPrograms || hasFunctionalStatus) {
      query.add(WHERE);

      if (hasFacilities) {
        query.add(format(FACILITY_PREDICATE, Joiner.on(COLLECTION_JOINER).join(facilityIds)));
      }

      if (hasPrograms) {
        if (hasFacilities) {
          query.add(AND);
        }

        query.add(format(PROGRAM_PREDICATE, Joiner.on(COLLECTION_JOINER).join(programIds)));
      }

      if (hasFunctionalStatus) {
        if (hasFacilities || hasPrograms) {
          query.add(AND);
        }

        query.add(format(STATUS_PREDICATE, functionalStatus.toString()));
      }
    }
  }

  private void addOrder(List<String> query, Pageable pageable) {
    if (count || null == pageable || null == pageable.getSort()) {
      return;
    }

    Iterator<Sort.Order> iterator = pageable.getSort().iterator();

    if (!iterator.hasNext()) {
      return;
    }

    query.add(ORDER_BY);

    while (iterator.hasNext()) {
      Sort.Order order = iterator.next();
      String property = order.getProperty();

      if (TYPE.equals(property)) {
        query.add(C_TYPE);
      } else {
        query.add("i." + property);
      }

      if (order.isAscending()) {
        query.add(ASC);
      } else {
        query.add(DESC);
      }

      if (iterator.hasNext()) {
        query.add(COMMA);
      }
    }
  }


}
