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

import java.util.List;
import java.util.UUID;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.openlmis.cce.domain.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

@JaversSpringDataAuditable
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public interface AlertRepository extends PagingAndSortingRepository<Alert, UUID>,
    BaseAuditableRepository<Alert, UUID> {

  @Query(value = "SELECT ca.*"
      + " FROM cce.cce_alerts ca"
      + " WHERE ca.active = :active" 
      + "   AND ca.inventoryitemid IN :inventoryItemIds" 
      + " ORDER BY ?#{#pageable}",
      nativeQuery = true
  )
  Page<Alert> findByActiveAndInventoryItemIdIn(
      @Param("active") Boolean active,
      @Param("inventoryItemIds") List<UUID> inventoryItemIds,
      Pageable pageable);

  @Query(value = "SELECT ca.*"
      + " FROM cce.cce_alerts ca"
      + " WHERE ca.active = :active" 
      + " ORDER BY ?#{#pageable}",
      nativeQuery = true
  )
  Page<Alert> findByActive(
      @Param("active") Boolean active,
      Pageable pageable);

  Page<Alert> findByInventoryItemIdIn(List<UUID> inventoryItemIds, Pageable pageable);
  
  boolean existsByExternalId(String externalId);
  
  Alert findByExternalId(String externalId);

  @Query(value = "SELECT\n"
      + "    ca.*\n"
      + "FROM\n"
      + "    cce.cce_alerts ca\n"
      + "WHERE\n"
      + "    id NOT IN (\n"
      + "        SELECT\n"
      + "            id\n"
      + "        FROM\n"
      + "            cce.cce_alerts ca\n"
      + "            INNER JOIN cce.jv_global_id g "
      + "ON CAST(ca.id AS varchar) = SUBSTRING(g.local_id, 2, 36)\n"
      + "            INNER JOIN cce.jv_snapshot s  ON g.global_id_pk = s.global_id_fk\n"
      + "    )\n"
      + " ORDER BY ?#{#pageable}",
      nativeQuery = true)
  Page<Alert> findAllWithoutSnapshots(Pageable pageable);
}
