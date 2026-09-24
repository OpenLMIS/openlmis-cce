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

package org.openlmis.cce.web.csv.parser;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.dto.CatalogItemDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.web.csv.model.ModelClass;
import org.openlmis.cce.web.csv.recordhandler.RecordProcessor;
import org.openlmis.cce.web.csv.recordhandler.RecordWriter;
import org.openlmis.cce.web.validator.CsvHeaderValidator;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class CsvParserTest {

  private static final int CHUNK_SIZE = 2;

  private static final String HEADER =
      "Type,Model,Manufacturer,Energy source,Storage temperature,From PQS catalog,Archived";

  @Mock
  private RecordProcessor<CatalogItemDto, CatalogItem> processor;

  @Mock
  private RecordWriter<CatalogItem> writer;

  @Mock
  private CsvHeaderValidator headerValidator;

  @Captor
  private ArgumentCaptor<List<CatalogItem>> chunkCaptor;

  private CsvParser csvParser;
  private ModelClass<CatalogItemDto> modelClass;

  @Before
  public void setUp() {
    csvParser = new CsvParser();
    ReflectionTestUtils.setField(csvParser, "chunkSize", CHUNK_SIZE);
    modelClass = new ModelClass<>(CatalogItemDto.class);

    when(processor.process(org.mockito.Matchers.any(CatalogItemDto.class)))
        .thenAnswer(invocation -> new CatalogItem());
  }

  @Test
  public void shouldWriteEveryChunkAndReturnTheRowCount() throws IOException {
    int count = parse(5);

    assertThat(count, equalTo(5));

    verify(writer, times(3)).write(chunkCaptor.capture());
    List<Integer> sizes = chunkCaptor.getAllValues()
        .stream()
        .map(List::size)
        .collect(Collectors.toList());
    assertThat(sizes, contains(CHUNK_SIZE, CHUNK_SIZE, 1));
  }

  @Test
  public void shouldWriteOnceWhenTheFileFitsInASingleChunk() throws IOException {
    int count = parse(CHUNK_SIZE);

    assertThat(count, equalTo(CHUNK_SIZE));
    verify(writer, times(1)).write(anyListOf(CatalogItem.class));
  }

  @Test
  public void shouldNotWriteAnythingForAFileWithHeadersOnly() throws IOException {
    int count = parse(0);

    assertThat(count, equalTo(0));
    verify(writer, times(0)).write(anyListOf(CatalogItem.class));
  }

  @Test
  public void shouldLetWriterFailuresEscapeUnwrapped() throws IOException {
    IllegalStateException failure = new IllegalStateException("writer refused the chunk");
    doThrow(failure).when(writer).write(anyListOf(CatalogItem.class));

    try {
      parse(5);
      fail("expected the writer failure to reach the caller");
    } catch (IllegalStateException actual) {
      assertThat(actual, sameInstance(failure));
    }

    verify(writer, times(1)).write(anyListOf(CatalogItem.class));
  }

  @Test(expected = ValidationMessageException.class)
  public void shouldThrowValidationExceptionForAnUnparseableRow() throws IOException {
    String csv = HEADER + "\nsometype,somemodel,somemanuf,NOT_AN_ENERGY_SOURCE,PLUS4,Y,N\n";

    csvParser.parse(stream(csv), modelClass, headerValidator, processor, writer);
  }

  private int parse(int rows) throws IOException {
    return csvParser.parse(stream(csv(rows)), modelClass, headerValidator, processor, writer);
  }

  private String csv(int rows) {
    String body = IntStream.range(0, rows)
        .mapToObj(i -> "sometype,model" + i + ",somemanuf,ELECTRIC,PLUS4,Y,N")
        .collect(Collectors.joining("\n"));
    return rows == 0 ? HEADER + "\n" : HEADER + "\n" + body + "\n";
  }

  private InputStream stream(String csv) {
    return new ByteArrayInputStream(csv.getBytes(UTF_8));
  }
}
