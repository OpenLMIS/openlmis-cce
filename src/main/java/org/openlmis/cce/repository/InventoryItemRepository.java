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

package org.openlmis.cce.repository;

import java.util.Optional;
import java.util.UUID;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.repository.custom.InventoryItemRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

@JaversSpringDataAuditable
public interface InventoryItemRepository extends PagingAndSortingRepository<InventoryItem, UUID>,
    InventoryItemRepositoryCustom,
    BaseAuditableRepository<InventoryItem, UUID> {

  Boolean existsByEquipmentTrackingIdAndCatalogItem_ModelAndCatalogItem_Type(
      String equipmentTrackingId, String catalogItemModel, String catalogItemType);

  @Query(value = "SELECT\n"
      + "    ci.*\n"
      + "FROM\n"
      + "    cce.cce_inventory_items ci\n"
      + "WHERE\n"
      + "    id NOT IN (\n"
      + "        SELECT\n"
      + "            id\n"
      + "        FROM\n"
      + "            cce.cce_inventory_items ci\n"
      + "            INNER JOIN cce.jv_global_id g "
      + "ON CAST(ci.id AS varchar) = SUBSTRING(g.local_id, 2, 36)\n"
      + "            INNER JOIN cce.jv_snapshot s  ON g.global_id_pk = s.global_id_fk\n"
      + "    )\n"
      + " ORDER BY ?#{#pageable}",
      nativeQuery = true)
  Page<InventoryItem> findAllWithoutSnapshots(Pageable pageable);

  @Query("SELECT SUM(i.catalogItem.netVolume) FROM InventoryItem i "
          + "WHERE i.facilityId = :facilityId AND i.functionalStatus = :functionalStatus")
  Optional<Number> getInventoryItemVolume(
          @Param("facilityId")UUID facilityId,
          @Param("functionalStatus") FunctionalStatus functionalStatus);
}
