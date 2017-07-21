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

package org.openlmis.cce.web.upload.recordhandler;

import org.openlmis.cce.domain.BaseEntity;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * CatalogItemPersistenceHandler is used for uploads of Catalog Item.
 * It uploads each catalog item record by record.
 */
@Component
public class CatalogItemPersistenceHandler
    extends AbstractPersistenceHandler<CatalogItem, CatalogItemDto> {

  @Autowired
  private CatalogItemRepository catalogItemRepository;

  @Override
  protected BaseEntity getExisting(CatalogItem record) {
    String equipmentCode = record.getEquipmentCode();
    if (equipmentCode != null) {
      return catalogItemRepository.findByEquipmentCode(equipmentCode);
    }
    return catalogItemRepository.findByTypeAndModel(record.getType(), record.getModel());
  }

  @Override
  protected CatalogItem importDto(CatalogItemDto record) {
    return CatalogItem.newInstance(record);
  }

  @Override
  protected void save(CatalogItem record) {
    catalogItemRepository.save(record);
  }

}