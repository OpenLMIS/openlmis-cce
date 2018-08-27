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

package org.openlmis.cce.web.csv.recordhandler;

import static java.util.Collections.singletonList;
import static org.assertj.core.util.Lists.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.repository.CatalogItemRepository;

@RunWith(MockitoJUnitRunner.class)
public class CatalogItemWriterTest {
  private static final String EQUIPMENT_CODE_PREFIX = "equipmentCode";
  private static final String MANUFACTURER_PREFIX = "manufacturer";
  private static final String MODEL_PREFIX = "model";

  private static final String EQUIPMENT_CODE_1 = EQUIPMENT_CODE_PREFIX + 1;
  private static final String EQUIPMENT_CODE_2 = EQUIPMENT_CODE_PREFIX + 2;
  private static final String EQUIPMENT_CODE_3 = EQUIPMENT_CODE_PREFIX + 3;

  private static final String MANUFACTURER_1 = MANUFACTURER_PREFIX + 1;
  private static final String MANUFACTURER_2 = MANUFACTURER_PREFIX + 2;
  private static final String MANUFACTURER_3 = MANUFACTURER_PREFIX + 3;
  private static final String MANUFACTURER_4 = MANUFACTURER_PREFIX + 4;

  private static final String MODEL_1 = MODEL_PREFIX + 1;
  private static final String MODEL_2 = MODEL_PREFIX + 2;
  private static final String MODEL_3 = MODEL_PREFIX + 3;
  private static final String MODEL_4 = MODEL_PREFIX + 4;


  @Captor
  private ArgumentCaptor<Iterable<CatalogItem>> catalogItemsCaptor;

  @Mock
  private CatalogItemRepository catalogItemRepository;

  @InjectMocks
  private CatalogItemWriter catalogItemWriter;

  @Test
  public void shouldNotSetIdIfExistingItemNotFound() {
    //given
    CatalogItem toSave = create(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1);
    List<CatalogItem> toSaveList = singletonList(toSave);

    // when
    when(catalogItemRepository.findExisting(anyListOf(CatalogItem.class)))
        .thenReturn(emptyList());

    catalogItemWriter.write(toSaveList);

    // then
    verify(catalogItemRepository).save(toSaveList);
  }

  @Test
  public void shouldFindByEquipmentCode() throws Exception {
    // given
    CatalogItem toSave = create(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1);
    List<CatalogItem> toSaveList = singletonList(toSave);

    CatalogItem db1 = create(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1);
    CatalogItem db2 = create(EQUIPMENT_CODE_2, MANUFACTURER_2, MODEL_2);
    CatalogItem db3 = create(null, MANUFACTURER_3, MODEL_3);
    CatalogItem db4 = create(null, MANUFACTURER_4, MODEL_4);
    List<CatalogItem> fromDb = Arrays.asList(db1, db2, db3, db4);

    // when
    when(catalogItemRepository.findExisting(anyListOf(CatalogItem.class)))
        .thenReturn(fromDb);

    catalogItemWriter.write(toSaveList);

    //then
    verify(catalogItemRepository).save(catalogItemsCaptor.capture());

    List<CatalogItem> captured = Lists.newArrayList(catalogItemsCaptor.getValue());
    assertThat(captured, hasSize(1));
    assertThat(captured.get(0).getId(), equalTo(db1.getId()));
  }

  @Test
  public void shouldFindByManufacturerAndModel() throws Exception {
    // given
    CatalogItem toSave = create(null, MANUFACTURER_3, MODEL_3);
    List<CatalogItem> toSaveList = singletonList(toSave);

    CatalogItem db1 = create(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1);
    CatalogItem db2 = create(EQUIPMENT_CODE_2, MANUFACTURER_2, MODEL_2);
    CatalogItem db3 = create(null, MANUFACTURER_3, MODEL_3);
    CatalogItem db4 = create(null, MANUFACTURER_4, MODEL_4);
    List<CatalogItem> fromDb = Arrays.asList(db1, db2, db3, db4);

    // when
    when(catalogItemRepository.findExisting(anyListOf(CatalogItem.class)))
        .thenReturn(fromDb);

    catalogItemWriter.write(toSaveList);

    //then
    verify(catalogItemRepository).save(catalogItemsCaptor.capture());

    List<CatalogItem> captured = Lists.newArrayList(catalogItemsCaptor.getValue());
    assertThat(captured, hasSize(1));
    assertThat(captured.get(0).getId(), equalTo(db3.getId()));
  }

  @Test
  public void shouldNotFindIfEquipmentCodeNotMatch() throws Exception {
    // given
    CatalogItem toSave = create(EQUIPMENT_CODE_3, MANUFACTURER_3, MODEL_3);
    List<CatalogItem> toSaveList = singletonList(toSave);

    CatalogItem db1 = create(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1);
    CatalogItem db2 = create(EQUIPMENT_CODE_2, MANUFACTURER_2, MODEL_2);
    CatalogItem db3 = create(null, MANUFACTURER_3, MODEL_3);
    CatalogItem db4 = create(null, MANUFACTURER_4, MODEL_4);
    List<CatalogItem> fromDb = Arrays.asList(db1, db2, db3, db4);

    // when
    when(catalogItemRepository.findExisting(anyListOf(CatalogItem.class)))
        .thenReturn(fromDb);

    catalogItemWriter.write(toSaveList);

    //then
    verify(catalogItemRepository).save(toSaveList);
  }

  private CatalogItem create(String equipmentCode, String manufacturer, String model) {
    CatalogItem item = new CatalogItem();
    item.setId(UUID.randomUUID());
    item.setEquipmentCode(equipmentCode);
    item.setManufacturer(manufacturer);
    item.setModel(model);

    return item;
  }

}