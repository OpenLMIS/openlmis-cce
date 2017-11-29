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

package org.openlmis.cce.repository.custom.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.openlmis.cce.repository.custom.impl.InventoryItemQueryBuilder.SELECT_SQL;

import org.junit.Test;

public class SelectInventoryItemQueryBuilderTest
    extends BaseInventoryItemQueryBuilderTest {

  private static final String ORDER_BY = " ORDER BY c.type ASC , i.functionalStatus DESC";

  @Override
  boolean isCount() {
    return false;
  }

  @Test
  public void shouldNotAddOrderPartIfSortIsNull() throws Exception {
    when(pageable.getSort()).thenReturn(null);

    String sql = new InventoryItemQueryBuilder(null, null, null, pageable, false).build();
    assertThat(sql, equalTo(SELECT_SQL));
  }

  @Test
  public void shouldAddOrderPartToSelectQuery() throws Exception {
    String sql = new InventoryItemQueryBuilder(null, null, null, pageable, false).build();
    assertThat(sql, equalTo(SELECT_SQL + ORDER_BY));
  }
}