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

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.openlmis.cce.i18n.PermissionMessageKeys.ERROR_NO_FOLLOWING_PERMISSION;

import java.util.UUID;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.dto.PermissionStringDto;
import org.openlmis.cce.exception.PermissionMessageException;
import org.openlmis.cce.util.AuthenticationHelper;
import org.openlmis.cce.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("PMD.TooManyMethods")
public class PermissionService {

  public static final String CCE_MANAGE = "CCE_MANAGE";
  public static final String CCE_INVENTORY_VIEW = "CCE_INVENTORY_VIEW";
  public static final String CCE_INVENTORY_EDIT = "CCE_INVENTORY_EDIT";
  public static final String CCE_INVENTORY_TRANSFER = "CCE_INVENTORY_TRANSFER";

  @Autowired
  private AuthenticationHelper authenticationHelper;

  @Autowired
  private PermissionStrings permissionStrings;

  @Value("${auth.server.clientId}")
  private String serviceTokenClientId;

  @Value("${auth.server.clientId.apiKey.prefix}")
  private String apiKeyPrefix;

  /**
   * Checks if current user has permission to manage CCE.
   *
   * @throws PermissionMessageException if the current user doesn't have the permission.
   */
  public void canManageCce() {
    checkPermission(CCE_MANAGE);
  }

  /**
   * Checks if current user has permission to view CCE inventory.
   *
   * @throws PermissionMessageException if the current user doesn't have the permission.
   */
  public void canViewInventory(InventoryItem inventory) {
    if (!hasPermission(CCE_INVENTORY_VIEW, inventory.getProgramId(), inventory.getFacilityId())) {
      throw new PermissionMessageException(
          new Message(ERROR_NO_FOLLOWING_PERMISSION, CCE_INVENTORY_VIEW));
    }
  }

  /**
   * Checks if current user has permission to edit CCE inventory.
   *
   * @throws PermissionMessageException if the current user doesn't have the permission.
   */
  public void canEditInventory(InventoryItem inventoryItem) {
    canEditInventory(inventoryItem.getProgramId(), inventoryItem.getFacilityId());
  }

  /**
   * Checks if current user has permission to edit CCE inventory.
   *
   * @throws PermissionMessageException if the current user doesn't have the permission.
   */
  public void canEditInventory(UUID programId, UUID facilityId) {
    if (!hasPermission(CCE_INVENTORY_EDIT, programId, facilityId)) {
      throw new PermissionMessageException(
          new Message(ERROR_NO_FOLLOWING_PERMISSION, CCE_INVENTORY_EDIT));
    }
  }

  /**
   * Checks if current client is either an API key, or current user has permission to edit CCE 
   * inventory.
   *
   * @throws PermissionMessageException if the current client is not an API key, or current user 
   *         doesn't have permission.
   */
  public void canEditInventoryOrIsApiKey(InventoryItem inventoryItem) {
    if (!hasPermission(CCE_INVENTORY_EDIT, inventoryItem.getProgramId(),
        inventoryItem.getFacilityId(), true, true, true)) {
      throw new PermissionMessageException(new Message(
          ERROR_NO_FOLLOWING_PERMISSION, CCE_INVENTORY_EDIT));
    }
  }

  private void checkPermission(String rightName) {
    if (!hasPermission(rightName)) {
      throw new PermissionMessageException(new Message(ERROR_NO_FOLLOWING_PERMISSION, rightName));
    }
  }

  private Boolean hasPermission(String rightName) {
    return hasPermission(rightName, null, null);
  }

  private Boolean hasPermission(String rightName, UUID program, UUID facility) {
    return hasPermission(rightName, program, facility, true, true, false);
  }

  private boolean hasPermission(String rightName, UUID program, UUID facility,
      boolean allowUserTokens, boolean allowServiceTokens,
      boolean allowApiKey) {
    OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder
        .getContext()
        .getAuthentication();

    return authentication.isClientOnly()
        ? checkServiceToken(allowServiceTokens, allowApiKey, authentication)
        : checkUserToken(rightName, program, facility, allowUserTokens);
  }

  private boolean checkUserToken(String rightName, UUID program, UUID facility,
      boolean allowUserTokens) {
    if (!allowUserTokens) {
      return false;
    }

    UUID user = authenticationHelper.getCurrentUser().getId();
    PermissionStrings.Handler handler = getPermissionStrings(user);
    PermissionStringDto permission = PermissionStringDto.create(rightName, facility, program);

    return handler.get().contains(permission);
  }

  private boolean checkServiceToken(boolean allowServiceTokens, boolean allowApiKey,
      OAuth2Authentication authentication) {
    String clientId = authentication.getOAuth2Request().getClientId();

    if (serviceTokenClientId.equals(clientId)) {
      return allowServiceTokens;
    }

    if (startsWith(clientId, apiKeyPrefix)) {
      return allowApiKey;
    }

    return false;
  }

  public PermissionStrings.Handler getPermissionStrings(UUID userId) {
    return permissionStrings.forUser(userId);
  }

}
