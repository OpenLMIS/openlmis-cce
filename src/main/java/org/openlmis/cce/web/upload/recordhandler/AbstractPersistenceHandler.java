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

/**
 * AbstractPersistenceHandler is a base class used for persisting each record of the uploaded file.
 */
public abstract class AbstractPersistenceHandler<T extends BaseEntity> implements RecordHandler {

  /**
   * Persists each record of the uploaded file.
   */
  public void execute(BaseEntity currentRecord) {
    BaseEntity existing = getExisting((T)currentRecord);

    if (existing != null) {
      currentRecord.setId(existing.getId());
    }

    save((T)currentRecord);
  }

  /**
   * Implementations should return an existing record, if there is one, based on however
   * the record's identity is determined.
   *
   * @param record the record an implementation should use to look for an "existing" record.
   * @return the record that exists that has the same identity as the given record.
   */
  protected abstract BaseEntity getExisting(T record);

  protected abstract void save(T record);
}
