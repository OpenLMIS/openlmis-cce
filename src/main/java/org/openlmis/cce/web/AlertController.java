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

import static org.openlmis.cce.service.ResourceNames.BASE_PATH;
import static org.openlmis.cce.web.AlertController.RESOURCE_PATH;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.openlmis.cce.domain.Alert;
import org.openlmis.cce.dto.AlertDto;
import org.openlmis.cce.repository.AlertRepository;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.openlmis.cce.service.PermissionService;
import org.openlmis.cce.util.Pagination;
import org.openlmis.cce.web.validator.AlertValidator;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Transactional
@RequestMapping(RESOURCE_PATH)
public class AlertController extends BaseController {

  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(AlertController.class);
  private static final String PROFILER_CHECK_PERMISSION = "CHECK_PERMISSION";

  static final String RESOURCE_PATH = BASE_PATH + "/cceAlerts";

  @Autowired
  AlertRepository alertRepository;

  @Autowired
  AlertValidator alertValidator;
  
  @Autowired
  PermissionService permissionService;
  
  @Autowired
  InventoryItemRepository inventoryItemRepository;
  
  /**
   * Creates or updates a CCE alert.
   *
   * @param alertDto DTO used to create or update a CCE alert.
   * @return the created or updated CCE alert.
   */
  @RequestMapping(method = RequestMethod.PUT)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public AlertDto saveAlert(@RequestBody AlertDto alertDto) {

    XLOGGER.entry(alertDto);
    Profiler profiler = new Profiler("CREATE_ALERT");
    profiler.setLogger(XLOGGER);

    profiler.start(PROFILER_CHECK_PERMISSION);
    permissionService.checkForApiKey();

    profiler.start("VALIDATE_ALERT");
    alertValidator.validate(alertDto);

    profiler.start("CREATE_DOMAIN_INSTANCE");
    alertDto.setInventoryItemRepository(inventoryItemRepository);
    Alert alert = Alert.newInstance(alertDto);

    profiler.start("SAVE");
    Alert savedAlert = alertRepository.save(alert);

    profiler.start("CREATE_RESPONSE_DTO");
    AlertDto responseDto = exportToDto(savedAlert);

    profiler.stop().log();
    XLOGGER.exit(responseDto);
    return responseDto;
  }

  /**
   * Get all CCE Inventory items that user has right for.
   *
   * @return CCE Inventory items.
   */
  @RequestMapping(method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Page<AlertDto> getAlertCollection(
      @RequestParam(value = "deviceId", required = false) List<UUID> deviceIds,
      @RequestParam(value = "active", required = false) Boolean active,
      Pageable pageable) {

    XLOGGER.entry(deviceIds, pageable);
    Profiler profiler = new Profiler("GET_ALERTS");
    profiler.setLogger(XLOGGER);

    Page<Alert> alertsPage;

    if (null != deviceIds && null != active) {
      profiler.start("FIND_BY_ACTIVE AND_INVENTORY_ITEM_IDS");
      alertsPage = alertRepository.findByActiveAndInventoryItemIdIn(active, deviceIds, pageable);
    } else if (null != active) {
      profiler.start("FIND_BY_ACTIVE");
      alertsPage = alertRepository.findByActive(active, pageable);
    } else if (null != deviceIds) {
      profiler.start("FIND_BY_INVENTORY_ITEM_IDS");
      alertsPage = alertRepository.findByInventoryItemIdIn(deviceIds, pageable);
    } else {
      profiler.start("FIND_ALL");
      alertsPage = alertRepository.findAll(pageable);
    }

    profiler.start("CREATE_DTOS");
    List<AlertDto> alertDtos = alertsPage.getContent().stream()
        .map(alert -> {
          AlertDto alertDto = new AlertDto();
          alert.export(alertDto);
          return alertDto;
        })
        .collect(Collectors.toList());

    profiler.start("CREATE_PAGE");
    Page<AlertDto> alertDtosPage = Pagination.getPage(alertDtos, pageable,
        alertsPage.getTotalElements());

    profiler.stop().log();
    XLOGGER.exit(alertDtosPage);
    return alertDtosPage;
  }

  private AlertDto exportToDto(Alert alert) {
    AlertDto dto = new AlertDto();
    alert.export(dto);
    return dto;
  }
}
