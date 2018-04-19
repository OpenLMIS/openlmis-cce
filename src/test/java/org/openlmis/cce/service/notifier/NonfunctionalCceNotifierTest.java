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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.EMAIL_NONFUNCTIONAL_CCE_CONTENT;
import static org.openlmis.cce.i18n.InventoryItemMessageKeys.EMAIL_NONFUNCTIONAL_CCE_SUBJECT;
import static org.openlmis.cce.service.PermissionService.CCE_INVENTORY_EDIT;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.ReasonNotWorkingOrNotInUse;
import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.dto.FacilityDto;
import org.openlmis.cce.dto.InventoryItemDto;
import org.openlmis.cce.dto.RightDto;
import org.openlmis.cce.dto.SupervisoryNodeDto;
import org.openlmis.cce.dto.UserDto;
import org.openlmis.cce.dto.UserObjectReferenceDto;
import org.openlmis.cce.i18n.MessageService;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.openlmis.cce.service.referencedata.FacilityReferenceDataService;
import org.openlmis.cce.service.referencedata.RightReferenceDataService;
import org.openlmis.cce.service.referencedata.SupervisoryNodeReferenceDataService;
import org.openlmis.cce.service.referencedata.UserReferenceDataService;
import org.openlmis.cce.util.Message;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.ZonedDateTime;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public class NonfunctionalCceNotifierTest {

  private static final String SUBJECT = "Attention: ${equipmentType} \"${referenceName}\" "
      + "at facility ${facilityName} on ${saveDate} is ${functionalStatus}";
  private static final String CONTENT = "Dear ${username}:\n"
      + "This email is to inform you that the ${equipmentType} \"${referenceName}\" "
      + "at ${facilityName} is has been marked as ${functionalStatus} with the "
      + "reason \"${reasonForNonFunctionalStatus}\". The last status update for "
      + "this device was made by user ${saveUser} at ${saveDate}.\n"
      + "Please login to view the list of non-functioning CCE needing attention"
      + "at this facility. ${urlToViewCceList}";
  private static final String TEST_KEY = "testKey";
  private static final String FACILITY_NAME = "some-facility";
  private static final String EQUIPMENT_TYPE = "eq-type";
  private static final String REFERENCE_NAME = "some-name";
  private static final FunctionalStatus FUNCTIONAL_STATUS = FunctionalStatus.AWAITING_REPAIR;
  private static final String LAST_MODIFIER_USERNAME = "lastmodifier";
  private static final String USERNAME = "user";
  private static final String URL_TO_VIEW_CCE = "http://localhost/#!/cce/inventory?page=0&size=10";
  private static final ReasonNotWorkingOrNotInUse REASON_NOT_WORKING_OR_NOT_IN_USE =
      ReasonNotWorkingOrNotInUse.DEAD;
  private static final ZonedDateTime MODIFIED_DATE = ZonedDateTime.now();

  @Mock
  private FacilityReferenceDataService facilityReferenceDataService;

  @Mock
  private NotificationService notificationService;

  @Mock
  private MessageService messageService;

  @Mock
  private RightReferenceDataService rightReferenceDataService;

  @Mock
  private SupervisoryNodeReferenceDataService supervisoryNodeReferenceDataService;

  @Mock
  private UserReferenceDataService userReferenceDataService;

  @Mock
  private CatalogItemRepository catalogItemRepository;

  @InjectMocks
  private NonfunctionalCceNotifier notifier;

  private UUID catalogItemId = UUID.randomUUID();
  private UUID facilityId = UUID.randomUUID();
  private UUID programId = UUID.randomUUID();
  private UUID supervisoryNodeId = UUID.randomUUID();
  private UUID rightId = UUID.randomUUID();
  private UUID lastModifierId = UUID.randomUUID();

  private InventoryItemDto inventoryItem = mock(InventoryItemDto.class);
  private CatalogItemDto catalogItemDto = mock(CatalogItemDto.class);
  private CatalogItem catalogItem = mock(CatalogItem.class);
  private UserDto user = mock(UserDto.class);
  private UserDto user2 = mock(UserDto.class);
  private FacilityDto facility = mock(FacilityDto.class);
  private SupervisoryNodeDto supervisoryNode = mock(SupervisoryNodeDto.class);
  private RightDto right = mock(RightDto.class);
  private UserDto lastModifier = mock(UserDto.class);
  private UserObjectReferenceDto lastModifierObj = mock(UserObjectReferenceDto.class);

  @Before
  public void setUp() {
    mockInventory();
    mockUsers(Collections.singletonList(user));
    mockMessages();
    ReflectionTestUtils.setField(notifier, "urlToViewCce", URL_TO_VIEW_CCE);
  }

  @Test
  public void shouldNotifyWithCorrectSubject() {
    notifier.notify(inventoryItem);

    verify(notificationService).notify(
        eq(user),
        eq(String.format("Attention: %s \"%s\" at facility %s on %s is %s",
            EQUIPMENT_TYPE, REFERENCE_NAME, FACILITY_NAME,
            getDateTimeFormatter().format(MODIFIED_DATE), FUNCTIONAL_STATUS)),
        any());
  }

  @Test
  public void shouldNotifyWithCorrectContent() {
    notifier.notify(inventoryItem);

    verify(notificationService).notify(
        eq(user),
        any(),
        eq(String.format("Dear %s:\n"
                + "This email is to inform you that the %s \"%s\" at %s is has been marked as %s"
                + " with the reason \"%s\". The last status update for this device was made by "
                + "user %s at %s.\n"
                + "Please login to view the list of non-functioning CCE needing attention"
                + "at this facility. %s",
            USERNAME, EQUIPMENT_TYPE, REFERENCE_NAME, FACILITY_NAME, FUNCTIONAL_STATUS,
            REASON_NOT_WORKING_OR_NOT_IN_USE, LAST_MODIFIER_USERNAME,
            getDateTimeFormatter().format(MODIFIED_DATE), URL_TO_VIEW_CCE)));
  }

  @Test
  public void shouldNotNotifyWhenEditorHaveNoEmail() {
    when(user.getEmail()).thenReturn(null);

    notifier.notify(inventoryItem);

    verify(notificationService, never()).notify(any(), any(), any());
  }

  @Test
  public void shouldNotNotifyWhenEditorIsNoActiveOrVerified() {
    when(user.activeAndVerified()).thenReturn(false);

    notifier.notify(inventoryItem);

    verify(notificationService, never()).notify(any(), any(), any());
  }

  @Test
  public void shouldNotNotifyWhenEditorDoesNotAllowNotify() {
    when(user.allowNotify()).thenReturn(false);

    notifier.notify(inventoryItem);

    verify(notificationService, never()).notify(any(), any(), any());
  }

  @Test
  public void shouldNotNotifyWhenUserIsNull() {
    when(supervisoryNodeReferenceDataService
        .findSupervisingUsers(supervisoryNodeId, rightId, programId))
        .thenReturn(Collections.emptyList());

    notifier.notify(inventoryItem);

    verify(notificationService, never()).notify(any(), any(), any());
  }

  @Test
  public void shouldNotNotifyWhenThereIsNoSupervisoryNodeForProgramAndFacility() {
    when(supervisoryNodeReferenceDataService
        .findSupervisoryNode(any(), any()))
        .thenReturn(null);

    notifier.notify(inventoryItem);

    verify(supervisoryNodeReferenceDataService).findSupervisoryNode(facilityId, programId);
    verify(supervisoryNodeReferenceDataService, never()).findSupervisingUsers(any(), any(), any());
    verify(notificationService, never()).notify(any(), any(), any());
  }

  @Test
  public void shouldNotifyTwoTimesForTwoUsersWithProperUsernameInContent() {
    mockUsers(Arrays.asList(user, user2));
    when(user2.getUsername()).thenReturn("user2-username");

    notifier.notify(inventoryItem);
    ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

    verify(notificationService, times(2))
        .notify(any(UserDto.class), any(), argument.capture());

    List<String> values = argument.getAllValues();

    assertTrue(values.get(0).contains(USERNAME));
    assertTrue(values.get(1).contains(user2.getUsername()));
  }

  private void mockInventory() {
    stubEquipmentType();
    when(inventoryItem.getFacilityId()).thenReturn(facilityId);
    stubFacilityName();
    when(inventoryItem.getFunctionalStatus()).thenReturn(FUNCTIONAL_STATUS);
    when(inventoryItem.getProgramId()).thenReturn(programId);
    when(inventoryItem.getReasonNotWorkingOrNotInUse())
        .thenReturn(REASON_NOT_WORKING_OR_NOT_IN_USE);
    stubLastModifierUsername();
    when(inventoryItem.getModifiedDate()).thenReturn(MODIFIED_DATE);
    when(inventoryItem.getReferenceName()).thenReturn(REFERENCE_NAME);
  }

  private void stubEquipmentType() {
    when(inventoryItem.getCatalogItem()).thenReturn(catalogItemDto);
    when(catalogItemDto.getId()).thenReturn(catalogItemId);
    when(catalogItemRepository.findOne(catalogItemId)).thenReturn(catalogItem);
    when(catalogItem.getType()).thenReturn(EQUIPMENT_TYPE);
  }

  private void stubFacilityName() {
    when(facilityReferenceDataService.findOne(facilityId)).thenReturn(facility);
    when(facility.getName()).thenReturn(FACILITY_NAME);
  }

  private void stubLastModifierUsername() {
    when(inventoryItem.getLastModifier()).thenReturn(lastModifierObj);
    when(lastModifierObj.getId()).thenReturn(lastModifierId);
    when(userReferenceDataService.findOne(lastModifierId)).thenReturn(lastModifier);
    when(lastModifier.getUsername()).thenReturn(LAST_MODIFIER_USERNAME);
  }

  private void mockUsers(List<UserDto> users) {
    stubUser(users);
    for (UserDto user : users) {
      when(user.allowNotify()).thenReturn(true);
      when(user.activeAndVerified()).thenReturn(true);
      when(user.getEmail()).thenReturn("user@mail.com");
      when(user.getUsername()).thenReturn(USERNAME);
    }
  }

  private void stubUser(List<UserDto> users) {
    when(supervisoryNodeReferenceDataService.findSupervisoryNode(facilityId, programId))
        .thenReturn(supervisoryNode);
    when(supervisoryNode.getId()).thenReturn(supervisoryNodeId);

    when(rightReferenceDataService.findRight(CCE_INVENTORY_EDIT)).thenReturn(right);
    when(right.getId()).thenReturn(rightId);

    when(supervisoryNodeReferenceDataService
        .findSupervisingUsers(supervisoryNodeId, rightId, programId))
        .thenReturn(users);
  }

  private void mockMessages() {
    Message.LocalizedMessage localizedMessage = new Message(TEST_KEY).new LocalizedMessage(SUBJECT);
    when(messageService.localize(new Message(EMAIL_NONFUNCTIONAL_CCE_SUBJECT)))
        .thenReturn(localizedMessage);
    localizedMessage = new Message(TEST_KEY).new LocalizedMessage(CONTENT);
    when(messageService.localize(new Message(EMAIL_NONFUNCTIONAL_CCE_CONTENT)))
        .thenReturn(localizedMessage);
  }

  private DateTimeFormatter getDateTimeFormatter() {
    Locale locale = LocaleContextHolder.getLocale();

    String datePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.MEDIUM, FormatStyle.MEDIUM, Chronology.ofLocale(locale), locale);
    return DateTimeFormatter.ofPattern(datePattern);
  }

}