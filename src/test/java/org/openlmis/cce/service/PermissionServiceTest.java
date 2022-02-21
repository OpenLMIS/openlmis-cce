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
import static org.openlmis.cce.service.OAuth2AuthenticationDataBuilder.API_KEY_PREFIX;
import static org.openlmis.cce.service.OAuth2AuthenticationDataBuilder.SERVICE_CLIENT_ID;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_EDIT;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_TRANSFER;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_VIEW;
import static org.openlmis.cce.service.PermissionService.CCE_MANAGE;

import java.util.Collections;
import java.util.UUID;
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
import org.springframework.test.util.ReflectionTestUtils;

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
  private InventoryItem inventoryItem;

  @InjectMocks
  private PermissionService permissionService;

  private SecurityContext securityContext;

  private OAuth2Authentication userClient;
  private OAuth2Authentication trustedClient;
  private OAuth2Authentication apiKeyClient;

  private UUID userId = UUID.randomUUID();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    initSecurityContext();
    when(authenticationHelper.getCurrentUser()).thenReturn(user);
    when(user.getId()).thenReturn(userId);
    when(permissionStrings.forUser(userId)).thenReturn(handler);

    ReflectionTestUtils.setField(permissionService, "serviceTokenClientId", SERVICE_CLIENT_ID);
    ReflectionTestUtils.setField(permissionService, "apiKeyPrefix", API_KEY_PREFIX);
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
    when(securityContext.getAuthentication()).thenReturn(trustedClient);

    permissionService.canManageCce();
  }

  @Test
  public void apiKeyCannotManageCce() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(apiKeyClient);
    exception.expect(PermissionMessageException.class);

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
    when(securityContext.getAuthentication()).thenReturn(trustedClient);

    permissionService.canViewInventory(inventoryItem);
  }

  @Test
  public void apiKeyCannotViewInventory() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(apiKeyClient);
    exception.expect(PermissionMessageException.class);

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
    when(securityContext.getAuthentication()).thenReturn(trustedClient);

    permissionService.canEditInventory(inventoryItem);
  }

  @Test
  public void apiKeyCannotEditInventory() throws Exception {
    when(securityContext.getAuthentication()).thenReturn(apiKeyClient);
    exception.expect(PermissionMessageException.class);

    permissionService.canEditInventory(inventoryItem);
  }
  
  @Test
  public void canEditInventoryOrIsApiKeyShouldAllowApiKeys() {
    when(securityContext.getAuthentication()).thenReturn(apiKeyClient);

    permissionService.canEditInventoryOrIsApiKey(inventoryItem);
  }
  
  @Test
  public void canEditInventoryOrIsApiKeyShouldAllowServiceTokens() {
    when(securityContext.getAuthentication()).thenReturn(trustedClient);

    permissionService.canEditInventoryOrIsApiKey(inventoryItem);
  }
  
  @Test
  public void canEditInventoryOrIsApiKeyShouldAllowUserTokensIfHasRight() {
    stubProgramAndFacilityInInventoryItem();
    stubHasRight(CCE_INVENTORY_EDIT, inventoryItem.getProgramId(), inventoryItem.getFacilityId());

    permissionService.canEditInventoryOrIsApiKey(inventoryItem);
  }

  @Test
  public void canEditInventoryOrIsApiKeyShouldNotAllowUserTokensIfDoesNotHaveRight() {
    stubProgramAndFacilityInInventoryItem();
    exception.expect(PermissionMessageException.class);
    exception.expectMessage(
        new Message(ERROR_NO_FOLLOWING_PERMISSION, CCE_INVENTORY_EDIT).toString());

    permissionService.canEditInventoryOrIsApiKey(inventoryItem);
  }

  @Test
  public void canTransferInventoryItem() {
    UUID targetProgramId = UUID.fromString("d835bb5b-2309-4c3e-b6d1-6315442b9f7b");
    UUID targetFacilityId = UUID.fromString("a337ec45-31a0-4f2b-9b2e-a105c4b669bb");

    stubProgramAndFacilityInInventoryItem();
    stubHasRight(CCE_INVENTORY_TRANSFER, targetProgramId, targetFacilityId);
    stubHasRight(
        CCE_INVENTORY_TRANSFER,
        inventoryItem.getProgramId(),
        inventoryItem.getFacilityId()
    );
    exception.expect(PermissionMessageException.class);

    permissionService.canTransferInventoryItem(inventoryItem, targetProgramId, targetFacilityId);
  }

  @Test
  public void canNotTransferInInventoryItem() {
    UUID targetProgramId = UUID.fromString("d835bb5b-2309-4c3e-b6d1-6315442b9f7b");
    UUID targetFacilityId = UUID.fromString("a337ec45-31a0-4f2b-9b2e-a105c4b669bb");

    stubProgramAndFacilityInInventoryItem();
    stubHasRight(CCE_INVENTORY_TRANSFER, targetProgramId, targetFacilityId);
    exception.expect(PermissionMessageException.class);

    permissionService.canTransferInventoryItem(
        inventoryItem,
        targetProgramId,
        targetFacilityId
    );
  }

  @Test
  public void canNotTransferOutInventoryItem() {
    stubProgramAndFacilityInInventoryItem();
    stubHasRight(
        CCE_INVENTORY_TRANSFER,
        inventoryItem.getProgramId(),
        inventoryItem.getFacilityId()
    );
    exception.expect(PermissionMessageException.class);

    permissionService.canTransferInventoryItem(
        inventoryItem,
        UUID.fromString("d835bb5b-2309-4c3e-b6d1-6315442b9f7b"),
        UUID.fromString("a337ec45-31a0-4f2b-9b2e-a105c4b669bb")
    );
  }

  private void stubProgramAndFacilityInInventoryItem() {
    when(inventoryItem.getProgramId()).thenReturn(UUID.randomUUID());
    when(inventoryItem.getFacilityId()).thenReturn(UUID.randomUUID());
  }

  private void initSecurityContext() {
    trustedClient = new OAuth2AuthenticationDataBuilder().buildServiceAuthentication();
    userClient = new OAuth2AuthenticationDataBuilder().buildUserAuthentication();
    apiKeyClient = new OAuth2AuthenticationDataBuilder().buildApiKeyAuthentication();

    securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(userClient);
  }

  private void stubHasRight(String rightName) {
    stubHasRight(rightName, null, null);
  }

  private void stubHasRight(String rightName, UUID programId, UUID faciliyId) {
    PermissionStringDto permission = PermissionStringDto.create(rightName, faciliyId, programId);
    when(handler.get()).thenReturn(Collections.singleton(permission));
  }

}
