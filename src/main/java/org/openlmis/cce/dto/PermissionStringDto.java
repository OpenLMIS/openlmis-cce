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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Object representation of single permission string.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PermissionStringDto {
  private String rightName;
  private UUID facilityId;
  private UUID programId;

  public static List<PermissionStringDto> from(List<String> permissionStrings) {
    return permissionStrings.stream().map(PermissionStringDto::from).collect(Collectors.toList());
  }

  /**
   * Parses string representation of permissionString to object representation.
   *
   * @param permissionString string representation
   * @return {@link PermissionStringDto}
   */
  public static PermissionStringDto from(String permissionString) {
    String[] elements = permissionString.split("\\|");
    String rightName = elements[0];
    UUID facilityId = elements.length > 1 ? UUID.fromString(elements[1]) : null;
    UUID programId = elements.length > 2 ? UUID.fromString(elements[2]) : null;

    return new PermissionStringDto(rightName, facilityId, programId);
  }

}