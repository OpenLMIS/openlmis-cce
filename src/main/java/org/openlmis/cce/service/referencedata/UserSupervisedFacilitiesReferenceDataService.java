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

import org.openlmis.cce.dto.FacilityDto;
import org.openlmis.cce.service.RequestParameters;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.UUID;

@Service
public class UserSupervisedFacilitiesReferenceDataService
    extends BaseReferenceDataService<FacilityDto> {

  @Override
  protected String getUrl() {
    return "/api/users/";
  }

  @Override
  protected Class<FacilityDto> getResultClass() {
    return FacilityDto.class;
  }

  @Override
  protected Class<FacilityDto[]> getArrayResultClass() {
    return FacilityDto[].class;
  }

  /**
   * Retrieves all the programs supervised by the given user from the reference data service.
   *
   * @param userId the UUID of the user
   * @param programId the UUID of the program
   * @param rightId the UUID of the right
   * @return a collection of facilities supervised by user
   */
  public Collection<FacilityDto> getFacilitiesSupervisedByUser(UUID userId, UUID programId,
                                                               UUID rightId) {
    return findAll(
        userId + "/supervisedFacilities",
        RequestParameters.init().set("rightId", rightId).set("programId", programId)
    );
  }
}
