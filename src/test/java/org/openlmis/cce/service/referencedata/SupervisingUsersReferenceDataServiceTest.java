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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.Test;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.service.RequestParameters;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SupervisingUsersReferenceDataServiceTest {

  private UUID supervisoryNode = UUID.randomUUID();
  private UUID right = UUID.randomUUID();
  private UUID program = UUID.randomUUID();

  @Test
  public void shouldFindAll() {
    SupervisingUsersReferenceDataService spy = spy(new SupervisingUsersReferenceDataService());
    List<UserDto> users = Collections.singletonList(spy(UserDto.class));
    doReturn(users)
        .when(spy)
        .findAll(eq(supervisoryNode + "/supervisingUsers"),
            refEq(RequestParameters.init().set("rightId", right).set("programId", program)));

    Collection<UserDto> foundUsers = spy.findAll(supervisoryNode, right, program);

    assertEquals(users, foundUsers);
  }

}