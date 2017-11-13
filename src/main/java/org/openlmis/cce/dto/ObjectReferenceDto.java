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

import lombok.Getter;
import lombok.Setter;
import org.openlmis.cce.web.BaseController;
import java.util.UUID;

public class ObjectReferenceDto extends BaseDto {

  public static final String SEPARATOR = "/";

  @Getter
  @Setter
  private String href;

  private ObjectReferenceDto() {}

  /**
   * Returns new object reference.
   *
   * @param id   object id
   */
  public ObjectReferenceDto(UUID id, String href) {
    setId(id);
    this.href = href;
  }

  public static ObjectReferenceDto ofFacility(UUID id, String serviceUrl) {
    return create(id, serviceUrl, "facilities");
  }

  public static ObjectReferenceDto create(UUID id, String serviceUrl, String resourceName) {
    return new ObjectReferenceDto(id,
        joinWith(SEPARATOR, serviceUrl + BaseController.API_PATH, resourceName, id));
  }

}
