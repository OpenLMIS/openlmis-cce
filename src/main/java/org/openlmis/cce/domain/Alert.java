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

package org.openlmis.cce.domain;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "cce_alerts")
@NoArgsConstructor
@Getter
public class Alert extends BaseEntity {

  @Column(columnDefinition = "VARCHAR(64)", unique = true, nullable = false)
  private String externalId;
  
  @Column(columnDefinition = TEXT, nullable = false)
  private String type;

  @ManyToOne
  @Type(type = UUID)
  @JoinColumn(name = "inventoryItemId", nullable = false)
  private InventoryItem inventoryItem;

  @Column(columnDefinition = "timestamp with time zone", nullable = false)
  private ZonedDateTime startTimestamp;

  @Column(columnDefinition = "timestamp with time zone")
  private ZonedDateTime endTimestamp;

  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "locale")
  @Column(name = "message")
  @CollectionTable(
      name = "cce_alert_status_messages",
      joinColumns = @JoinColumn(name = "alertid"))
  private Map<String, String> statusMessages;

  @Column(columnDefinition = "timestamp with time zone")
  private ZonedDateTime dismissTimestamp;
  
  @Column(columnDefinition = "bool")
  private Boolean active;

  private Alert(String externalId, String type, InventoryItem inventoryItem,
      ZonedDateTime startTimestamp, ZonedDateTime endTimestamp, Map<String, String> statusMessages,
      ZonedDateTime dismissTimestamp) {
    this.externalId = externalId;
    this.type = type;
    this.inventoryItem = inventoryItem;
    this.startTimestamp = startTimestamp;
    this.endTimestamp = endTimestamp;
    this.statusMessages = statusMessages;
    this.dismissTimestamp = dismissTimestamp;
    setActive();
  }

  public static Alert createNew(String externalId, String type, InventoryItem inventoryItem,
      ZonedDateTime startTimestamp, ZonedDateTime endTimestamp, Map<String, String> statusMessages,
      ZonedDateTime dismissTimestamp) {
    return new Alert(externalId, type, inventoryItem, startTimestamp, endTimestamp, statusMessages,
        dismissTimestamp);
  }

  /**
   * Creates new instance based on data from {@link Alert.Importer}
   *
   * @param importer instance of {@link Alert.Importer}
   * @return new instance of Alert.
   */
  public static Alert newInstance(Alert.Importer importer) {
    return new Alert(importer.getExternalId(),
        importer.getType(),
        importer.getInventoryItem(),
        importer.getStartTimestamp(),
        importer.getEndTimestamp(),
        importer.getStatusMessages(),
        importer.getDismissTimestamp());
  }

  /**
   * Fills in null values to this alert from the other alert.
   *
   * @param otherAlert other alert to fill in from
   */
  public void fillInFrom(Alert otherAlert) {
    id = otherAlert.id;
    if (null == endTimestamp) {
      endTimestamp = otherAlert.endTimestamp;
    }
    if (null == dismissTimestamp) {
      dismissTimestamp = otherAlert.dismissTimestamp;
    }
    setActive();
  }

  /**
   * This should always be called when related properties are set.
   */
  private void setActive() {
    active = (null == endTimestamp && null == dismissTimestamp);
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setExternalId(externalId);
    exporter.setType(type);
    exporter.setInventoryItem(inventoryItem);
    exporter.setStartTimestamp(startTimestamp);
    exporter.setEndTimestamp(endTimestamp);
    exporter.setStatusMessages(Collections.unmodifiableMap(new HashMap<>(statusMessages)));
    exporter.setDismissTimestamp(dismissTimestamp);
  }

  public interface Exporter {

    void setExternalId(String externalId);

    void setType(String type);

    void setInventoryItem(InventoryItem inventoryItem);

    void setStartTimestamp(ZonedDateTime startTimestamp);

    void setEndTimestamp(ZonedDateTime endTimestamp);

    void setStatusMessages(Map<String, String> statusMessages);

    void setDismissTimestamp(ZonedDateTime dismissTimestamp);
  }

  public interface Importer {

    String getExternalId();

    String getType();

    InventoryItem getInventoryItem();
    
    ZonedDateTime getStartTimestamp();

    ZonedDateTime getEndTimestamp();

    Map<String, String> getStatusMessages();

    ZonedDateTime getDismissTimestamp();
  }
}
