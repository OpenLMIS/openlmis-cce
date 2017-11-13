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

import org.openlmis.cce.dto.SupervisoryNodeDto;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.service.RequestParameters;
import org.openlmis.cce.service.ResourceNames;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SupervisoryNodeReferenceDataService
    extends BaseReferenceDataService<SupervisoryNodeDto> {

  @Override
  protected String getUrl() {
    return ResourceNames.getSupervisoryNodesPath();
  }

  @Override
  protected Class<SupervisoryNodeDto> getResultClass() {
    return SupervisoryNodeDto.class;
  }

  @Override
  protected Class<SupervisoryNodeDto[]> getArrayResultClass() {
    return SupervisoryNodeDto[].class;
  }

  /**
   * Find a correct supervisory node by the provided facility and program.
   */
  public SupervisoryNodeDto findSupervisoryNode(UUID facility, UUID program) {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("facilityId", facility);
    requestBody.put("programId", program);

    List<SupervisoryNodeDto> nodes = getPage("search", RequestParameters.init(), requestBody)
        .getContent();

    return isEmpty(nodes) ? null : nodes.get(0);
  }

  /**
   * Get a list of supervising users for a certain supervisory node, program and right.
   *
   * @param supervisoryNode the UUID of the supervisory node.
   * @param right the UUID of the right.
   * @param program the UUID of the program.
   * @return a collection of supervising users.
   */
  public Collection<UserDto> findSupervisingUsers(UUID supervisoryNode, UUID right, UUID program) {
    RequestParameters params = RequestParameters.init()
        .set("rightId", right)
        .set("programId", program);

    return findAll(supervisoryNode + "/supervisingUsers", params, UserDto[].class);
  }

}
