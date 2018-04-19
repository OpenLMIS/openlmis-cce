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

package org.openlmis.cce.service.notifier;

import static org.openlmis.cce.i18n.InventoryItemMessageKeys.EMAIL_NONFUNCTIONAL_CCE_CONTENT;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.EMAIL_NONFUNCTIONAL_CCE_SUBJECT;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.ERROR_USER_INVALID;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_EDIT;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.dto.RightDto;
import org.openlmis.cce.dto.SupervisoryNodeDto;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.openlmis.cce.service.referencedata.FacilityReferenceDataService;
import org.openlmis.cce.service.referencedata.RightReferenceDataService;
import org.openlmis.cce.service.referencedata.SupervisoryNodeReferenceDataService;
import org.openlmis.cce.service.referencedata.UserReferenceDataService;
import org.openlmis.cce.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NonfunctionalCceNotifier extends BaseNotifier {

  @Autowired
  private RightReferenceDataService rightReferenceDataService;

  @Autowired
  private SupervisoryNodeReferenceDataService supervisoryNodeReferenceDataService;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private FacilityReferenceDataService facilityReferenceDataService;

  @Autowired
  private UserReferenceDataService userReferenceDataService;

  @Autowired
  private CatalogItemRepository catalogItemRepository;

  @Value("${email.urlToViewCce}")
  private String urlToViewCce;

  /**
   * Notify user with Edit Inventory rights for the facility assigned with inventory item.
   *
   * @param inventoryItem InventoryItem that became non functional
   */
  public void notify(InventoryItemDto inventoryItem) {
    Collection<UserDto> recipients = getRecipients(inventoryItem);

    logger.debug("Found recipients to send notification to: {}", recipients);
    if (!recipients.isEmpty()) {
      String subject = getMessage(EMAIL_NONFUNCTIONAL_CCE_SUBJECT);
      String content = getMessage(EMAIL_NONFUNCTIONAL_CCE_CONTENT);

      Map<String, String> valuesMap = getValuesMap(inventoryItem);
      StrSubstitutor sub = new StrSubstitutor(valuesMap);
      for (UserDto recipient : recipients) {
        if (canBeNotified(recipient)) {
          valuesMap.put("username", recipient.getUsername());
          logger.debug("Sending notification to: " + recipient.getUsername());
          notificationService.notify(recipient, sub.replace(subject), sub.replace(content));
        }
      }
    }
  }

  @NotNull
  private Collection<UserDto> getRecipients(InventoryItemDto inventoryItem) {
    SupervisoryNodeDto supervisoryNode = supervisoryNodeReferenceDataService
        .findSupervisoryNode(inventoryItem.getFacilityId(), inventoryItem.getProgramId());
    if (supervisoryNode == null) {
      logger.warn("There is no supervisory node for program {} and facility {}",
          inventoryItem.getProgramId(), inventoryItem.getFacilityId());
      return Collections.emptySet();
    }
    logger.debug("Supervisory node found: " + supervisoryNode.getName());

    RightDto right = rightReferenceDataService.findRight(CCE_INVENTORY_EDIT);
    return supervisoryNodeReferenceDataService
        .findSupervisingUsers(supervisoryNode.getId(), right.getId(), inventoryItem.getProgramId());
  }

  private Map<String, String> getValuesMap(InventoryItemDto inventoryItem) {
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("equipmentType", getType(inventoryItem));
    valuesMap.put("facilityName", getFacilityName(inventoryItem.getFacilityId()));
    valuesMap.put("functionalStatus", inventoryItem.getFunctionalStatus().toString());
    valuesMap.put("referenceName", inventoryItem.getReferenceName());
    valuesMap.put("reasonForNonFunctionalStatus",
        inventoryItem.getReasonNotWorkingOrNotInUse().toString());
    valuesMap.put("saveUser", getUsername(inventoryItem));
    valuesMap.put("saveDate", getDateTimeFormatter().format(inventoryItem.getModifiedDate()));
    valuesMap.put("urlToViewCceList", urlToViewCce);
    return valuesMap;
  }

  private String getType(InventoryItemDto inventoryItem) {
    return catalogItemRepository.findOne(inventoryItem.getCatalogItem().getId()).getType();
  }

  private String getFacilityName(UUID facilityId) {
    return facilityReferenceDataService.findOne(facilityId).getName();
  }

  private String getUsername(InventoryItemDto inventoryItem) {
    UUID userId = inventoryItem.getLastModifier().getId();
    UserDto one = userReferenceDataService.findOne(userId);
    if (one == null) {
      throw new ValidationMessageException(
          new Message(ERROR_USER_INVALID, userId));
    }
    return one.getUsername();
  }

}
