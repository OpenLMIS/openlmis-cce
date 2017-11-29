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
import static org.openlmis.cce.repository.custom.impl.InventoryItemQueryBuilder.COUNT_SQL;
import static org.openlmis.cce.repository.custom.impl.InventoryItemQueryBuilder.FACILITY_PREDICATE;
import static org.openlmis.cce.repository.custom.impl.InventoryItemQueryBuilder.PROGRAM_PREDICATE;
import static org.openlmis.cce.repository.custom.impl.InventoryItemQueryBuilder.SELECT_SQL;
import static org.openlmis.cce.repository.custom.impl.InventoryItemQueryBuilder.STATUS_PREDICATE;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.domain.FunctionalStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseInventoryItemQueryBuilderTest {
  private static final UUID PROGRAM = UUID.randomUUID();
  private static final UUID FACILITY = UUID.randomUUID();
  private static final FunctionalStatus STATUS = FunctionalStatus.FUNCTIONING;

  private static final Collection<UUID> PROGRAMS = Collections.singleton(PROGRAM);
  private static final Collection<UUID> FACILITIES = Collections.singleton(FACILITY);

  private static final String FACILITY_WHERE = String.format(FACILITY_PREDICATE, FACILITY);
  private static final String PROGRAM_WHERE = String.format(PROGRAM_PREDICATE, PROGRAM);
  private static final String STATUS_WHERE = String.format(STATUS_PREDICATE, STATUS.toString());
  private static final String FACILITY_PROGRAM_WHERE = FACILITY_WHERE + " AND " + PROGRAM_WHERE;
  private static final String FACILITY_PROGRAM_STATUS_WHERE = FACILITY_PROGRAM_WHERE + " AND "
      + STATUS_WHERE;

  private static final String WHERE = " WHERE ";

  private static final String TYPE = "type";
  private static final String FUNCTIONAL_STATUS = "functionalStatus";

  @Mock
  Pageable pageable;

  abstract boolean isCount();

  private String getSelect() {
    return isCount() ? COUNT_SQL : SELECT_SQL;
  }

  @Before
  public void setUp() throws Exception {
    Sort sort = new Sort(ASC, TYPE).and(new Sort(DESC, FUNCTIONAL_STATUS));
    when(pageable.getSort()).thenReturn(sort);
  }

  @Test
  public void shouldCreateSimpleQuery() throws Exception {
    String sql = new InventoryItemQueryBuilder(null, null, null, null, isCount()).build();
    assertThat(sql, equalTo(getSelect()));
  }

  @Test
  public void shouldAddFacilityWherePartToQuery() throws Exception {
    String sql = new InventoryItemQueryBuilder(FACILITIES, null, null, null, isCount()).build();
    assertThat(sql, equalTo(getSelect() + WHERE + FACILITY_WHERE));
  }

  @Test
  public void shouldAddProgramWherePartToQuery() throws Exception {
    String sql = new InventoryItemQueryBuilder(null, PROGRAMS, null ,null, isCount()).build();
    assertThat(sql, equalTo(getSelect() + WHERE + PROGRAM_WHERE));
  }

  @Test
  public void shouldAddFacilityAndProgramWherePartsToQuery() throws Exception {
    String sql = new InventoryItemQueryBuilder(FACILITIES, PROGRAMS, null, null, isCount()).build();
    assertThat(sql, equalTo(getSelect() + WHERE + FACILITY_PROGRAM_WHERE));
  }

  @Test
  public void shouldAddFunctionalStatusToQuery() throws Exception {
    String sql = new InventoryItemQueryBuilder(
        null, null, STATUS, null, isCount()).build();
    assertThat(sql, equalTo(getSelect() + WHERE + STATUS_WHERE));
  }

  @Test
  public void shouldAddFacilityProgramAndStatusWherePartsToQuery() throws Exception {
    String sql = new InventoryItemQueryBuilder(
        FACILITIES, PROGRAMS, STATUS, null, isCount()).build();
    assertThat(sql, equalTo(getSelect() + WHERE + FACILITY_PROGRAM_STATUS_WHERE));
  }

}