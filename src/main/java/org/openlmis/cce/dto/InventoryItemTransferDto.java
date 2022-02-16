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

package org.openlmis.cce.dto;

import java.util.UUID;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;


public class InventoryItemTransferDto {

  private final UUID facilityId;
  private final UUID programId;
  private final int yearOfInstallation;

  /**
   * Constructor.
   */
  @JsonCreator
  public InventoryItemTransferDto(
      @JsonProperty UUID facilityId,
      @JsonProperty UUID programId,
      @JsonProperty int yearOfInstallation
  ) {
    this.facilityId = facilityId;
    this.programId = programId;
    this.yearOfInstallation = yearOfInstallation;
  }

  public UUID getFacilityId() {
    return facilityId;
  }

  public UUID getProgramId() {
    return programId;
  }

  public int getYearOfInstallation() {
    return yearOfInstallation;
  }

}
