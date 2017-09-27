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

import org.apache.commons.collections.CollectionUtils;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.repository.custom.InvetoryItemRepositoryCustom;
import org.openlmis.cce.util.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class InventoryItemRepositoryImpl implements InvetoryItemRepositoryCustom {

  private static final String FACILITY_ID = "facilityId";
  private static final String PROGRAM_ID = "programId";
  private static final String TYPE = "type";
  private static final String CATALOG_ITEM = "catalogItem";

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * This method is supposed to retrieve all inventory items with matched parameters.
   * Result is sorted and paginated by pageable parameter.
   *
   * @param facilityIds list of facility ids
   * @param programIds  list of program ids
   * @param pageable    pagination and sort parameters
   * @return Page of Catalog Items matching the parameters.
   */
  public Page<InventoryItem> search(List<UUID> facilityIds,
                                    List<UUID> programIds,
                                    Pageable pageable) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();

    CriteriaQuery<InventoryItem> query = builder.createQuery(InventoryItem.class);
    CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);

    query = prepareQuery(query, facilityIds, programIds, pageable, false);
    countQuery = prepareQuery(countQuery, facilityIds, programIds, pageable, true);

    Long count = entityManager.createQuery(countQuery).getSingleResult();

    List<InventoryItem> result = entityManager.createQuery(query)
        .setMaxResults(pageable.getPageSize())
        .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
        .getResultList();

    return Pagination.getPage(result, pageable, count);
  }

  private <T> CriteriaQuery<T> prepareQuery(CriteriaQuery<T> query,
                                            List<UUID> facilityIds,
                                            List<UUID> programIds,
                                            Pageable pageable,
                                            boolean count) {

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    Root<InventoryItem> root = query.from(InventoryItem.class);

    if (count) {
      CriteriaQuery<Long> countQuery = (CriteriaQuery<Long>) query;
      query = (CriteriaQuery<T>) countQuery.select(builder.count(root));
    }

    Predicate predicate = builder.conjunction();

    if (!CollectionUtils.isEmpty(facilityIds)) {
      Predicate facilityPredicate = builder.disjunction();
      for (UUID facilityId : facilityIds) {
        facilityPredicate = builder.or(facilityPredicate,
            builder.equal(root.get(FACILITY_ID), facilityId));
      }
      predicate = builder.and(predicate, facilityPredicate);
    }

    if (!CollectionUtils.isEmpty(programIds)) {
      Predicate programPredicate = builder.disjunction();
      for (UUID programId : programIds) {
        programPredicate = builder.or(programPredicate,
            builder.equal(root.get(PROGRAM_ID), programId));
      }
      predicate = builder.and(predicate, programPredicate);
    }

    query.where(predicate);

    if (!count && pageable != null && pageable.getSort() != null) {
      query = addSortProperties(query, root, pageable);
    }

    return query;
  }

  private <T> CriteriaQuery<T> addSortProperties(CriteriaQuery<T> query,
                                                 Root root, Pageable pageable) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    List<Order> orders = new ArrayList<>();
    Iterator<Sort.Order> iterator = pageable.getSort().iterator();
    Sort.Order order;

    while (iterator.hasNext()) {
      order = iterator.next();
      String property = order.getProperty();

      Path path;
      if (TYPE.equals(property)) {
        path = root.join(CATALOG_ITEM, JoinType.LEFT).get(TYPE);
      } else {
        path = root.get(property);
      }
      if (order.isAscending()) {
        orders.add(builder.asc(path));
      } else {
        orders.add(builder.desc(path));
      }
    }
    return query.orderBy(orders);
  }
}
