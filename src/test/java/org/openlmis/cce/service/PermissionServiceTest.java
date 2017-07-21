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
import static org.openlmis.cce.i18n.MessageKeys.ERROR_NO_FOLLOWING_PERMISSION;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_EDIT;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_VIEW;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.cce.dto.ResultDto;
import org.openlmis.cce.dto.RightDto;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.exception.PermissionMessageException;
import org.openlmis.cce.service.referencedata.UserReferenceDataService;
import org.openlmis.cce.util.AuthenticationHelper;
import org.openlmis.cce.util.Message;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import java.util.UUID;

@SuppressWarnings("PMD.TooManyMethods")
public class PermissionServiceTest {
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Mock
  private UserReferenceDataService userReferenceDataService;

  @Mock
  private AuthenticationHelper authenticationHelper;

  @Mock
  private RightDto cceManageRight;

  @Mock
  private RightDto viewInventoryRight;

  @Mock
  private RightDto editInventoryRight;

  @Mock
  private UserDto user;

  @Mock
  private OAuth2Authentication userClient;

  @Mock
  private OAuth2Authentication trustedClient;

  @InjectMocks
  private PermissionService permissionService;

  private SecurityContext securityContext;
  private UUID userId = UUID.randomUUID();
  private UUID cceManageRightId = UUID.randomUUID();
  private UUID viewInventoryRightId = UUID.randomUUID();
  private UUID editInventoryRightId = UUID.randomUUID();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    initSecurityContext();
    when(authenticationHelper.getCurrentUser()).thenReturn(user);
    when(user.getId()).thenReturn(userId);

    when(authenticationHelper.getRight(PermissionService.CCE_MANAGE))
        .thenReturn(cceManageRight);
    when(cceManageRight.getId()).thenReturn(cceManageRightId);

    when(authenticationHelper.getRight(CCE_INVENTORY_VIEW))
        .thenReturn(viewInventoryRight);
    when(viewInventoryRight.getId()).thenReturn(viewInventoryRightId);

    when(authenticationHelper.getRight(CCE_INVENTORY_EDIT))
        .thenReturn(editInventoryRight);
    when(editInventoryRight.getId()).thenReturn(editInventoryRightId);
  }

  @Test
  public void userClientCanManageCceIfHasRight() throws Exception {
    stubHasRight(cceManageRightId);

    permissionService.canManageCce();
  }

  @Test
  public void userClientCannotManageCceIfHasNoRight() throws Exception {
    exception.expect(PermissionMessageException.class);
    exception.expectMessage(
        new Message(ERROR_NO_FOLLOWING_PERMISSION, PermissionService.CCE_MANAGE).toString());

    permissionService.canManageCce();
  }

  @Test
  public void trustedClientCanManageCce() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(trustedClient);

    permissionService.canManageCce();
  }

  @Test
  public void userClientCanViewInventoryIfHasRight() throws Exception {
    stubHasRight(viewInventoryRightId);

    permissionService.canViewInventory();
  }

  @Test
  public void userClientCannotViewInventoryIfHasNoRight() throws Exception {
    exception.expect(PermissionMessageException.class);
    exception.expectMessage(
        new Message(ERROR_NO_FOLLOWING_PERMISSION, CCE_INVENTORY_VIEW).toString());

    permissionService.canViewInventory();
  }

  @Test
  public void trustedClientCanViewInventory() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(trustedClient);

    permissionService.canViewInventory();
  }

  @Test
  public void userClientCanEditInventoryIfHasRight() throws Exception {
    stubHasRight(editInventoryRightId);

    permissionService.canEditInventory();
  }

  @Test
  public void userClientCannotEditInventoryIfHasNoRight() throws Exception {
    exception.expect(PermissionMessageException.class);
    exception.expectMessage(
        new Message(ERROR_NO_FOLLOWING_PERMISSION, CCE_INVENTORY_EDIT).toString());

    permissionService.canEditInventory();
  }

  @Test
  public void trustedClientCanEditInventory() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(trustedClient);

    permissionService.canEditInventory();
  }

  private void initSecurityContext() {
    securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(userClient);
    when(userClient.isClientOnly()).thenReturn(false);
    when(trustedClient.isClientOnly()).thenReturn(true);
  }

  private void stubHasRight(UUID rightId) {
    when(userReferenceDataService.hasRight(userId, rightId))
        .thenReturn(new ResultDto<>(true));
  }

}