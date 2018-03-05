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
import java.util.UUID;
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
import lombok.Setter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "cce_alerts")
@NoArgsConstructor
@Getter
@Setter
public class Alert extends BaseEntity {

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

  @Column(columnDefinition = "bool")
  private Boolean dismissed;

  private Alert(String type, InventoryItem inventoryItem, ZonedDateTime startTimestamp, 
      ZonedDateTime endTimestamp, Map<String, String> statusMessages, Boolean dismissed) {
    this.type = type;
    this.inventoryItem = inventoryItem;
    this.startTimestamp = startTimestamp;
    this.endTimestamp = endTimestamp;
    this.statusMessages = statusMessages;
    this.dismissed = dismissed;
  }

  public static Alert createNew(String type, InventoryItem inventoryItem,
      ZonedDateTime startTimestamp, ZonedDateTime endTimestamp, Map<String, String> statusMessages,
      Boolean dismissed) {
    return new Alert(type, inventoryItem, startTimestamp, endTimestamp, statusMessages, dismissed);
  }

  /**
   * Creates new instance based on data from {@link Alert.Importer}
   *
   * @param importer instance of {@link Alert.Importer}
   * @return new instance of Alert.
   */
  public static Alert newInstance(Alert.Importer importer) {
    Alert alert = new Alert(importer.getType(),
        importer.getInventoryItem(),
        importer.getStartTimestamp(),
        importer.getEndTimestamp(),
        importer.getStatusMessages(),
        importer.getDismissed());
    alert.id = importer.getId();
    return alert;
  }

  /**
   * Fills in null values to this alert from the other alert.
   *
   * @param otherAlert other alert to fill in from
   */
  public void fillInFrom(Alert otherAlert) {
    if (null == endTimestamp) {
      endTimestamp = otherAlert.endTimestamp;
    }
    if (null == dismissed) {
      dismissed = otherAlert.dismissed;
    }
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setId(id);
    exporter.setType(type);
    exporter.setInventoryItem(inventoryItem);
    exporter.setStartTimestamp(startTimestamp);
    exporter.setEndTimestamp(endTimestamp);
    exporter.setStatusMessages(Collections.unmodifiableMap(new HashMap<>(statusMessages)));
    exporter.setDismissed(dismissed);
  }

  public interface Exporter {

    void setId(UUID id);

    void setType(String type);

    void setInventoryItem(InventoryItem inventoryItem);

    void setStartTimestamp(ZonedDateTime startTimestamp);

    void setEndTimestamp(ZonedDateTime endTimestamp);

    void setStatusMessages(Map<String, String> statusMessages);

    void setDismissed(Boolean dismissed);
  }

  public interface Importer {

    UUID getId();

    String getType();

    InventoryItem getInventoryItem();
    
    ZonedDateTime getStartTimestamp();

    ZonedDateTime getEndTimestamp();

    Map<String, String> getStatusMessages();

    Boolean getDismissed();
  }
}
