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

package org.openlmis.cce;

import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.service.InventoryItemSearchParams;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InventoryItemSearchParamsDataBuilder {
  private UUID facilityId = UUID.randomUUID();
  private FunctionalStatus functionalStatus = FunctionalStatus.FUNCTIONING;
  private List<String> expands = Arrays.asList("program");

  public InventoryItemSearchParamsDataBuilder withFacilityId(UUID newFacilityId) {
    this.facilityId = newFacilityId;
    return this;
  }

  public InventoryItemSearchParamsDataBuilder withExpands(List<String> newExpands) {
    this.expands = newExpands;
    return this;
  }

  public InventoryItemSearchParamsDataBuilder withoutFacilityId() {
    return withFacilityId(null);
  }

  public InventoryItemSearchParamsDataBuilder withoutExpands() {
    this.expands = null;
    return this;
  }

  public InventoryItemSearchParamsDataBuilder withoutFunctionalStatus() {
    return withFunctionalStatus(null);
  }

  /**
   * Creates new instance of {@link InventoryItem} with provided data.
   *
   * @return the new instance of {@link InventoryItem}
   */
  public InventoryItemSearchParams build() {
    return new InventoryItemSearchParams(
        facilityId,
        functionalStatus,
        expands
    );
  }

  /**
   * Creates new instance of {@link InventoryItem} with only the facility functional status set.
   *
   * @return the new instance of {@link InventoryItem}
   */
  public static InventoryItemSearchParams buildWithOnlyFunctionalstatus() {
    return new InventoryItemSearchParamsDataBuilder()
        .withoutFacilityId()
        .withoutExpands()
        .build();
  }

  /**
   * Creates new instance of {@link InventoryItem} without any parameters set.
   *
   * @return the new instance of {@link InventoryItem}
   */
  public static InventoryItemSearchParams buildEmpty() {
    return new InventoryItemSearchParamsDataBuilder()
        .withoutFacilityId()
        .withoutExpands()
        .withoutFunctionalStatus()
        .build();
  }

  private InventoryItemSearchParamsDataBuilder withFunctionalStatus(
      FunctionalStatus newFunctionalStatus) {

    this.functionalStatus = newFunctionalStatus;
    return this;
  }
}
