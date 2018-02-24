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
public interface AlertRepository extends PagingAndSortingRepository<Alert, UUID> {

  @Query(value = "SELECT ca.*"
      + " FROM cce.cce_alerts ca"
      + " WHERE (ca.endtimestamp IS NULL AND ca.dismissed = false) = :active" 
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
      + " WHERE (ca.endtimestamp IS NULL AND ca.dismissed = false) = :active" 
      + " ORDER BY ?#{#pageable}",
      nativeQuery = true
  )
  Page<Alert> findByActive(
      @Param("active") Boolean active,
      Pageable pageable);

  Page<Alert> findByInventoryItemIdIn(List<UUID> inventoryItemIds, Pageable pageable);
}
