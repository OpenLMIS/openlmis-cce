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

package org.openlmis.cce.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.openlmis.cce.i18n.PermissionMessageKeys.ERROR_NO_FOLLOWING_PERMISSION;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_EDIT;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_VIEW;
import static org.openlmis.cce.service.PermissionService.CCE_MANAGE;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.PermissionStringDto;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.exception.PermissionMessageException;
import org.openlmis.cce.util.AuthenticationHelper;
import org.openlmis.cce.util.Message;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Collections;
import java.util.UUID;

@SuppressWarnings("PMD.TooManyMethods")
public class PermissionServiceTest {
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Mock
  private AuthenticationHelper authenticationHelper;

  @Mock
  private PermissionStrings permissionStrings;

  @Mock
  private UserDto user;

  @Mock
  private PermissionStrings.Handler handler;

  @Mock
  private OAuth2Authentication userAuthentication;

  @Mock
  private OAuth2Authentication clientAuthentication;

  @Mock
  private InventoryItem inventoryItem;

  @InjectMocks
  private PermissionService permissionService;

  private SecurityContext securityContext;
  private UUID userId = UUID.randomUUID();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    initSecurityContext();
    when(authenticationHelper.getCurrentUser()).thenReturn(user);
    when(user.getId()).thenReturn(userId);
    when(permissionStrings.forUser(userId)).thenReturn(handler);
  }

  @Test
  public void userCanManageCceIfHasRight() throws Exception {
    stubHasRight(CCE_MANAGE);

    permissionService.canManageCce();
  }

  @Test
  public void userCannotManageCceIfHasNoRight() throws Exception {
    exception.expect(PermissionMessageException.class);
    exception.expectMessage(
        new Message(ERROR_NO_FOLLOWING_PERMISSION, CCE_MANAGE).toString());

    permissionService.canManageCce();
  }

  @Test
  public void clientAppCanManageCce() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(clientAuthentication);

    permissionService.canManageCce();
  }

  @Test
  public void userCanViewInventoryIfHasRight() throws Exception {
    stubProgramAndFacilityInInventoryItem();
    stubHasRight(CCE_INVENTORY_VIEW, inventoryItem.getProgramId(), inventoryItem.getFacilityId());

    permissionService.canViewInventory(inventoryItem);
  }

  @Test
  public void userCannotViewInventoryIfHasNoRight() throws Exception {
    stubProgramAndFacilityInInventoryItem();
    exception.expect(PermissionMessageException.class);
    exception.expectMessage(
        new Message(ERROR_NO_FOLLOWING_PERMISSION, CCE_INVENTORY_VIEW).toString());

    permissionService.canViewInventory(inventoryItem);
  }

  @Test
  public void clientAppCanViewInventory() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(clientAuthentication);

    permissionService.canViewInventory(inventoryItem);
  }

  @Test
  public void userCanEditInventoryIfHasRight() throws Exception {
    stubProgramAndFacilityInInventoryItem();
    stubHasRight(CCE_INVENTORY_EDIT, inventoryItem.getProgramId(), inventoryItem.getFacilityId());

    permissionService.canEditInventory(inventoryItem);
  }

  @Test
  public void userCannotEditInventoryIfHasNoRight() throws Exception {
    stubProgramAndFacilityInInventoryItem();
    exception.expect(PermissionMessageException.class);
    exception.expectMessage(
        new Message(ERROR_NO_FOLLOWING_PERMISSION, CCE_INVENTORY_EDIT).toString());

    permissionService.canEditInventory(inventoryItem);
  }

  @Test
  public void clientAppCanEditInventory() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(clientAuthentication);

    permissionService.canEditInventory(inventoryItem);
  }

  private void stubProgramAndFacilityInInventoryItem() {
    when(inventoryItem.getProgramId()).thenReturn(UUID.randomUUID());
    when(inventoryItem.getFacilityId()).thenReturn(UUID.randomUUID());
  }

  private void initSecurityContext() {
    securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(userAuthentication);
    when(userAuthentication.isClientOnly()).thenReturn(false);
    when(clientAuthentication.isClientOnly()).thenReturn(true);
  }

  private void stubHasRight(String rightName) {
    stubHasRight(rightName, null, null);
  }

  private void stubHasRight(String rightName, UUID programId, UUID faciliyId) {
    PermissionStringDto permission = PermissionStringDto.create(rightName, faciliyId, programId);
    when(handler.get()).thenReturn(Collections.singleton(permission));
  }

}