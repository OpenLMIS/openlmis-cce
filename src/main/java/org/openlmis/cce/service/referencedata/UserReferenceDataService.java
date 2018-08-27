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

package org.openlmis.cce.service.referencedata;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.service.RequestParameters;
import org.openlmis.cce.service.ResourceNames;
import org.openlmis.cce.service.ServiceResponse;
import org.springframework.stereotype.Service;

@Service
public class UserReferenceDataService extends BaseReferenceDataService<UserDto> {

  @Override
  protected String getUrl() {
    return ResourceNames.getUsersPath();
  }

  @Override
  protected Class<UserDto> getResultClass() {
    return UserDto.class;
  }

  @Override
  protected Class<UserDto[]> getArrayResultClass() {
    return UserDto[].class;
  }

  /**
   * This method retrieves a user with given name.
   *
   * @param name the name of user.
   * @return UserDto containing user's data, or null if such user was not found.
   */
  public UserDto findUser(String name) {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("username", name);

    List<UserDto> users = getPage("search", RequestParameters.init(), requestBody).getContent();
    return isEmpty(users) ? null : users.get(0);
  }

  public ServiceResponse<List<String>> getPermissionStrings(UUID user, String etag) {
    return tryFindAll(user + "/permissionStrings", String[].class, etag);
  }
}
