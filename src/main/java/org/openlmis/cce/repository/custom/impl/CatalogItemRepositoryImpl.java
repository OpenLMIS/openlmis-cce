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

import static org.openlmis.cce.domain.CatalogItem.EQUIPMENT_CODE;
import static org.openlmis.cce.domain.CatalogItem.MANUFACTURER;
import static org.openlmis.cce.domain.CatalogItem.MODEL;

import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.repository.custom.CatalogItemRepositoryCustom;
import org.openlmis.cce.util.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class CatalogItemRepositoryImpl implements CatalogItemRepositoryCustom {

  private static final String TYPE = "type";
  private static final String ARCHIVED = "archived";
  private static final String VISIBLE_IN_CATALOG = "visibleInCatalog";

  @PersistenceContext
  private EntityManager entityManager;

  /**
   * This method is supposed to retrieve all catalog items with matched parameters. To find all
   * wanted Catalog Items by type we use criteria query and equals operator.
   *
   * @param type             type of catalog item
   * @param archived         if catalog item is archived
   * @param visibleInCatalog if catalog item is visible in catalog
   * @param pageable         pagination parameters
   * @return List of Catalog Items matching the parameters.
   */
  public Page<CatalogItem> search(String type, Boolean archived,
                                  Boolean visibleInCatalog, Pageable pageable) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();

    CriteriaQuery<CatalogItem> query = builder.createQuery(CatalogItem.class);
    CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);

    query = prepareQuery(query, type, archived, visibleInCatalog, false);
    countQuery = prepareQuery(countQuery, type, archived, visibleInCatalog, true);

    Long count = entityManager.createQuery(countQuery).getSingleResult();

    List<CatalogItem> result = entityManager.createQuery(query)
        .setMaxResults(pageable.getPageSize())
        .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
        .getResultList();

    return Pagination.getPage(result, pageable, count);
  }

  @Override
  public List<CatalogItem> findExisting(List<CatalogItem> items) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<CatalogItem> query = builder.createQuery(CatalogItem.class);
    Root<CatalogItem> root = query.from(CatalogItem.class);

    int size = items.size();
    Predicate[] predicates = new Predicate[size];

    for (int i = 0; i < size; i++) {
      CatalogItem catalogItem = items.get(i);
      Predicate predicate;

      if (null != catalogItem.getEquipmentCode()) {
        predicate = builder.equal(root.get(EQUIPMENT_CODE), catalogItem.getEquipmentCode());
      } else {
        predicate = builder.and(
            builder.equal(root.get(MANUFACTURER), catalogItem.getManufacturer()),
            builder.equal(root.get(MODEL), catalogItem.getModel())
        );
      }

      predicates[i] = predicate;
    }

    query.where(builder.or(predicates));

    return entityManager.createQuery(query).getResultList();
  }

  private <T> CriteriaQuery<T> prepareQuery(CriteriaQuery<T> query, String type,
                                            Boolean archived, Boolean visibleInCatalog,
                                            boolean count) {

    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    Root<CatalogItem> root = query.from(CatalogItem.class);

    if (count) {
      CriteriaQuery<Long> countQuery = (CriteriaQuery<Long>) query;
      query = (CriteriaQuery<T>) countQuery.select(builder.count(root));
    }

    Predicate predicate = builder.conjunction();

    if (type != null) {
      predicate = builder.and(predicate, builder.equal(root.get(TYPE), type));
    }

    if (archived != null) {
      predicate = builder.and(predicate, builder.equal(root.get(ARCHIVED), archived));
    }

    if (visibleInCatalog != null) {
      predicate = builder.and(predicate,
          builder.equal(root.get(VISIBLE_IN_CATALOG), visibleInCatalog));
    }

    query.where(predicate);

    return query;
  }
}
