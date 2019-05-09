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

package org.openlmis.cce;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.openlmis.cce.domain.Alert;
import org.openlmis.cce.domain.InventoryItem;

@SuppressWarnings({"PMD.TooManyMethods"})
public class AlertDataBuilder {

  private UUID id;
  private String externalId;
  private String type;
  private InventoryItem inventoryItem;
  private ZonedDateTime startTimestamp;
  private ZonedDateTime endTimestamp;
  private Map<String, String> statusMessages;
  private ZonedDateTime dismissTimestamp;

  /**
   * Returns instance of {@link AlertDataBuilder} with sample data.
   */
  public AlertDataBuilder() {
    id = UUID.randomUUID();
    externalId = UUID.randomUUID().toString();
    type = "Equipment needs attention: too hot";
    inventoryItem = new InventoryItemDataBuilder().build();
    startTimestamp = ZonedDateTime.now();
    endTimestamp = ZonedDateTime.now().plusHours(1);
    statusMessages = new HashMap<>();
    dismissTimestamp = ZonedDateTime.now();
  }

  /**
   * Creates new instance of {@link Alert} without id.
   */
  public Alert buildAsNew() {
    return Alert.createNew(externalId, type, inventoryItem, startTimestamp,
        endTimestamp, statusMessages, dismissTimestamp);
  }

  /**
   * Creates new instance of {@link Alert}.
   */
  public Alert build() {
    Alert alert = buildAsNew();
    alert.setId(id);

    return alert;
  }

  public AlertDataBuilder withExternalId(String externalId) {
    this.externalId = externalId;
    return this;
  }

  public AlertDataBuilder withType(String type) {
    this.type = type;
    return this;
  }

  public AlertDataBuilder withInventoryItem(InventoryItem inventoryItem) {
    this.inventoryItem = inventoryItem;
    return this;
  }

  public AlertDataBuilder withStartTimestamp(ZonedDateTime startTimestamp) {
    this.startTimestamp = startTimestamp;
    return this;
  }

  public AlertDataBuilder withDismissTimestamp(ZonedDateTime dismissTimestamp) {
    this.dismissTimestamp = dismissTimestamp;
    return this;
  }

  public AlertDataBuilder withoutDismissTimestamp() {
    this.dismissTimestamp = null;
    return this;
  }

  public AlertDataBuilder withEndTimestamp(ZonedDateTime endTimestamp) {
    this.endTimestamp = endTimestamp;
    return this;
  }

  public AlertDataBuilder withoutEndTimestamp() {
    this.endTimestamp = null;
    return this;
  }

  public AlertDataBuilder withStatusMessages(Map<String, String> statusMessages) {
    this.statusMessages = statusMessages;
    return this;
  }
}
