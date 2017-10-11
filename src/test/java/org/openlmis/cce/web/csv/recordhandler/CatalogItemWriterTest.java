package org.openlmis.cce.web.csv.recordhandler;

import static org.assertj.core.util.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.repository.CatalogItemRepository;

import java.util.Collections;
import java.util.UUID;


public class CatalogItemWriterTest {

  @Captor
  private ArgumentCaptor<Iterable<CatalogItem>> catalogItemsCaptor;

  private static final String EQCODE = "eqcode";
  private static final String SOME_MAKE = "someManufacturer";
  private static final String SOME_MODEL = "someModel";

  @Mock
  private CatalogItemRepository catalogItemRepository;

  @InjectMocks
  private CatalogItemWriter catalogItemWriter;

  private CatalogItem catalogItem;
  private Iterable<CatalogItem> catalogItems;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    catalogItem = new CatalogItem();
    catalogItem.setEquipmentCode(EQCODE);

    catalogItems = Collections.singletonList(catalogItem);
  }

  @Test
  public void shouldSetIdIfExistingItemFoundByCode() {
    CatalogItem existingCatalogItem = new CatalogItem();
    existingCatalogItem.setId(UUID.randomUUID());

    when(catalogItemRepository.existsByEquipmentCode(EQCODE)).thenReturn(true);
    when(catalogItemRepository.findByEquipmentCode(EQCODE)).thenReturn(existingCatalogItem);

    catalogItemWriter.write(catalogItems);

    verify(catalogItemRepository).save(catalogItemsCaptor.capture());
    assertEquals(
        existingCatalogItem.getId(),
        newArrayList(catalogItemsCaptor.getValue()).get(0).getId()
    );
  }

  @Test
  public void shouldSetIdIfExistingItemFoundByTypeAndModel() {
    CatalogItem existingCatalogItem = new CatalogItem();
    existingCatalogItem.setId(UUID.randomUUID());

    catalogItem.setEquipmentCode(null);
    catalogItem.setManufacturer(SOME_MAKE);
    catalogItem.setModel(SOME_MODEL);

    when(catalogItemRepository.existsByEquipmentCode(EQCODE)).thenReturn(false);
    when(catalogItemRepository.existsByManufacturerAndModel(SOME_MAKE, SOME_MODEL))
        .thenReturn(true);
    when(catalogItemRepository.findByManufacturerAndModel(SOME_MAKE, SOME_MODEL))
        .thenReturn(existingCatalogItem);

    catalogItemWriter.write(catalogItems);

    verify(catalogItemRepository).save(catalogItemsCaptor.capture());
    assertEquals(
        existingCatalogItem.getId(),
        newArrayList(catalogItemsCaptor.getValue()).get(0).getId()
    );
  }

  @Test
  public void shouldNotSetIdIfExistingItemNotFound() {
    when(catalogItemRepository.existsByEquipmentCode(EQCODE)).thenReturn(false);
    when(catalogItemRepository.existsByManufacturerAndModel(SOME_MAKE, SOME_MODEL))
        .thenReturn(false);

    catalogItemWriter.write(catalogItems);

    verify(catalogItemRepository).save(catalogItems);
  }

}