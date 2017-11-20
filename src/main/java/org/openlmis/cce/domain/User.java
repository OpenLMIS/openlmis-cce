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

import static org.openlmis.cce.domain.BaseEntity.TEXT;
import static org.openlmis.cce.domain.BaseEntity.UUID;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Type(type = UUID)
  private UUID id;

  @Column(columnDefinition = TEXT)
  private String firstName;

  @Column(columnDefinition = TEXT)
  private String lastName;

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setLastModifierId(id);
    exporter.setFirstName(firstName);
    exporter.setLastName(lastName);
  }

  public interface Exporter {
    void setLastModifierId(UUID lastModifierId);

    void setFirstName(String firstName);

    void setLastName(String lastName);
  }

}
