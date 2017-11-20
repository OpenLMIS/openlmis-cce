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

import static org.apache.commons.lang3.StringUtils.joinWith;
import static org.openlmis.cce.service.ResourceNames.BASE_PATH;
import static org.openlmis.cce.service.ResourceNames.SEPARATOR;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openlmis.cce.domain.User;
import java.util.UUID;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserObjectReferenceDto extends ObjectReferenceDto implements User.Exporter {

  @Getter
  @Setter
  private String firstName;

  @Getter
  @Setter
  private String lastName;

  /**
   * Creates new UserObjectReferenceDto.
   */
  public static UserObjectReferenceDto create(User user, String serviceUrl, String resourceName) {

    UserObjectReferenceDto dto = new UserObjectReferenceDto();
    user.export(dto);

    dto.setHref(joinWith(SEPARATOR, serviceUrl + BASE_PATH, resourceName, dto.getId()));

    return dto;
  }

  @Override
  public void setLastModifierId(UUID lastModifierId) {
    setId(lastModifierId);
  }
}
