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

package org.openlmis.cce.web.fhir;

import static org.openlmis.cce.service.ResourceNames.getLocationPath;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.openlmis.cce.domain.InventoryItem;

@Getter
public class Device extends Resource {
  private final String manufacturer;
  private final String model;
  private final Reference location;
  private final List<SerialNumberIdentifier> identifier;

  Device(String serviceUrl, InventoryItem inventory) {
    super(inventory.getId(), "Device");

    this.manufacturer = inventory.getCatalogItem().getManufacturer();
    this.model = inventory.getCatalogItem().getModel();
    this.location = new Reference(serviceUrl, getLocationPath(), inventory.getFacilityId());
    this.identifier = Collections.singletonList(
        new SerialNumberIdentifier(inventory.getEquipmentTrackingId()));
  }

}
