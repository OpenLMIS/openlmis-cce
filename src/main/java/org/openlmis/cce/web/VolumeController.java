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

import java.util.Optional;
import java.util.UUID;

import org.openlmis.cce.dto.VolumeDto;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.openlmis.cce.web.validator.VolumeValidator;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VolumeController extends BaseController {

  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(VolumeController.class);

  @Autowired
  private InventoryItemRepository inventoryRepository;

  @Autowired
  private VolumeValidator volumeValidator;

  /**
   * Get all volume CCE Inventory items for specified facility.
   *
   * @return volume
   */
  @GetMapping(value = "inventoryItems/volume")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public VolumeDto getVolumeForFacilityId(
          @RequestParam(value = "facilityId", required = true) String facilityId) {
    XLOGGER.entry(facilityId);
    Profiler profiler = new Profiler("GET_VOLUME_INVENTORY_BY_FACILITY_ID");
    profiler.setLogger(XLOGGER);

    profiler.start("VALIDATE");
    volumeValidator.validate(facilityId);

    profiler.start("FIND_VOLUME_IN_DB");
    Optional<Number> volume = inventoryRepository
            .getFacilityFunctioningVolume(UUID.fromString(facilityId));

    profiler.start("CREATE_VOLUME_DTO");
    VolumeDto volumeDto = new VolumeDto(volume.orElse(0).intValue());

    profiler.stop().log();
    XLOGGER.exit(volume);
    return volumeDto;
  }
}
