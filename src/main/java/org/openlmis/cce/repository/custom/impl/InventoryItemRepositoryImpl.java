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

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.repository.custom.InventoryItemRepositoryCustom;
import org.openlmis.cce.util.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class InventoryItemRepositoryImpl implements InventoryItemRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * This method is supposed to retrieve all inventory items with matched parameters. Result is
   * sorted and paginated by pageable parameter. You can use 'type' sort value and it will sort
   * inventory items by their catalog item type.
   *
   * @param facilityIds list of facility ids
   * @param programIds  list of program ids
   * @param pageable    pagination and sort parameters
   * @return Page of Catalog Items matching the parameters.
   */
  public Page<InventoryItem> search(Collection<UUID> facilityIds, Collection<UUID> programIds,
                                    FunctionalStatus functionalStatus, Pageable pageable) {
    TypedQuery<Long> count = createQuery(
        facilityIds, programIds, functionalStatus, pageable, Long.class
    );

    TypedQuery<InventoryItem> select = createQuery(
        facilityIds, programIds, functionalStatus, pageable, InventoryItem.class
    );

    List<InventoryItem> list = select.getResultList();
    Long size = count.getSingleResult();

    return Pagination.getPage(list, pageable, size);
  }

  private <T> TypedQuery<T> createQuery(Collection<UUID> facilities, Collection<UUID> programs,
                                        FunctionalStatus functionalStatus, Pageable pageable,
                                        Class<T> type) {
    boolean isNumber = Number.class.isAssignableFrom(type);
    String sql = new InventoryItemQueryBuilder(facilities, programs, functionalStatus, pageable,
        isNumber).build();

    TypedQuery<T> query = entityManager.createQuery(sql, type);

    if (!isNumber && null != pageable) {
      query
          .setMaxResults(pageable.getPageSize())
          .setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
    }

    return query;
  }

}
