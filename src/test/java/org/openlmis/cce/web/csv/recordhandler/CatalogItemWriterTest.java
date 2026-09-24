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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.anyListOf;
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
import org.mockito.junit.MockitoJUnitRunner;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.repository.CatalogItemRepository;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"PMD.TooManyMethods"})
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
    CatalogItem toSave = incoming(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1);

    when(catalogItemRepository.findExisting(anyListOf(CatalogItem.class)))
        .thenReturn(emptyList());

    catalogItemWriter.write(singletonList(toSave));

    assertThat(captureSaved().get(0).getId(), nullValue());
  }

  @Test
  public void shouldFindByEquipmentCodeAndModel() {
    CatalogItem toSave = incoming(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1);
    CatalogItem db1 = create(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1);

    givenInDatabase(db1,
        create(EQUIPMENT_CODE_2, MANUFACTURER_2, MODEL_2),
        create(null, MANUFACTURER_3, MODEL_3),
        create(null, MANUFACTURER_4, MODEL_4));

    catalogItemWriter.write(singletonList(toSave));

    assertThat(captureSaved().get(0).getId(), equalTo(db1.getId()));
  }

  @Test
  public void shouldFindByManufacturerAndModel() {
    CatalogItem toSave = incoming(null, MANUFACTURER_3, MODEL_3);
    CatalogItem db3 = create(null, MANUFACTURER_3, MODEL_3);

    givenInDatabase(create(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1), db3);

    catalogItemWriter.write(singletonList(toSave));

    assertThat(captureSaved().get(0).getId(), equalTo(db3.getId()));
  }

  @Test
  public void shouldFindByManufacturerAndModelWhenTheEquipmentCodeIsNew() {
    // the database enforces unique (manufacturer, model), so a row whose manufacturer and model
    // already exist is that item, whatever equipment code the file gives it
    CatalogItem toSave = incoming(EQUIPMENT_CODE_3, MANUFACTURER_3, MODEL_3);
    CatalogItem db3 = create(null, MANUFACTURER_3, MODEL_3);

    givenInDatabase(create(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1), db3);

    catalogItemWriter.write(singletonList(toSave));

    assertThat(captureSaved().get(0).getId(), equalTo(db3.getId()));
  }

  @Test
  public void shouldGiveEachRowItsOwnIdWhenTwoItemsShareAnEquipmentCode() {
    // (equipmentCode, model) is unique, equipmentCode alone is not
    CatalogItem db1 = create(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1);
    CatalogItem db2 = create(EQUIPMENT_CODE_1, MANUFACTURER_2, MODEL_2);

    givenInDatabase(db1, db2);

    CatalogItem first = incoming(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1);
    CatalogItem second = incoming(EQUIPMENT_CODE_1, MANUFACTURER_2, MODEL_2);

    catalogItemWriter.write(Arrays.asList(first, second));

    List<CatalogItem> saved = captureSaved();
    assertThat(saved, hasSize(2));
    assertThat(saved.get(0).getId(), equalTo(db1.getId()));
    assertThat(saved.get(1).getId(), equalTo(db2.getId()));
  }

  @Test
  public void shouldNotMatchRowsWithoutAnEquipmentCodeOnModelAlone() {
    CatalogItem db1 = create(null, MANUFACTURER_1, MODEL_1);

    givenInDatabase(db1);

    CatalogItem toSave = incoming(null, MANUFACTURER_2, MODEL_1);

    catalogItemWriter.write(singletonList(toSave));

    assertThat(captureSaved().get(0).getId(), nullValue());
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldRejectARowMatchingTwoDifferentItems() {
    CatalogItem byCode = create(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1);
    CatalogItem byManufacturer = create(null, MANUFACTURER_2, MODEL_1);

    givenInDatabase(byCode, byManufacturer);

    catalogItemWriter.write(singletonList(incoming(EQUIPMENT_CODE_1, MANUFACTURER_2, MODEL_1)));
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldRejectTwoRowsResolvingToTheSameItem() {
    CatalogItem db1 = create(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1);

    givenInDatabase(db1);

    catalogItemWriter.write(Arrays.asList(
        incoming(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1),
        incoming(null, MANUFACTURER_1, MODEL_1)));
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldRejectTwoBrandNewRowsSharingManufacturerAndModel() {
    givenInDatabase();

    catalogItemWriter.write(Arrays.asList(
        incoming(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1),
        incoming(EQUIPMENT_CODE_2, MANUFACTURER_1, MODEL_1)));
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldRejectTwoBrandNewRowsSharingEquipmentCodeAndModel() {
    givenInDatabase();

    catalogItemWriter.write(Arrays.asList(
        incoming(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1),
        incoming(EQUIPMENT_CODE_1, MANUFACTURER_2, MODEL_1)));
  }

  @Test
  public void shouldAcceptBrandNewRowsSharingOnlyTheEquipmentCode() {
    givenInDatabase();

    catalogItemWriter.write(Arrays.asList(
        incoming(EQUIPMENT_CODE_1, MANUFACTURER_1, MODEL_1),
        incoming(EQUIPMENT_CODE_1, MANUFACTURER_2, MODEL_2)));

    assertThat(captureSaved(), hasSize(2));
  }

  private void givenInDatabase(CatalogItem... items) {
    when(catalogItemRepository.findExisting(anyListOf(CatalogItem.class)))
        .thenReturn(Arrays.asList(items));
  }

  private List<CatalogItem> captureSaved() {
    verify(catalogItemRepository).saveAll(catalogItemsCaptor.capture());
    return Lists.newArrayList(catalogItemsCaptor.getValue());
  }

  /**
   * A row as it arrives from the CSV: the file carries no id column, so incoming items never have
   * one. Only rows already in the database do.
   */
  private CatalogItem incoming(String equipmentCode, String manufacturer, String model) {
    CatalogItem item = new CatalogItem();
    item.setEquipmentCode(equipmentCode);
    item.setManufacturer(manufacturer);
    item.setModel(model);
    return item;
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
