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

package org.openlmis.cce.web;

import static org.openlmis.cce.util.Pagination.handlePage;

import org.openlmis.cce.repository.InventoryItemRepository;
import org.openlmis.cce.web.fhir.Device;
import org.openlmis.cce.web.fhir.DeviceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class DeviceController extends BaseController {

  @Autowired
  private InventoryItemRepository inventoryItemRepository;

  @Autowired
  private DeviceFactory deviceFactory;

  /**
   * Gets FHIR devices.
   */
  @GetMapping("/Device")
  @ResponseStatus(HttpStatus.OK)
  public List<Device> getDevices() {
    List<Device> list = new ArrayList<>();

    handlePage(
        inventoryItemRepository::findAll,
        inventory -> list.add(deviceFactory.createFor(inventory))
    );

    return list;
  }
}
