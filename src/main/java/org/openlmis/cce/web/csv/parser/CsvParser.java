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

import static java.util.concurrent.CompletableFuture.runAsync;
import static org.openlmis.cce.i18n.CsvUploadMessageKeys.ERROR_UPLOAD_RECORD_INVALID;

import com.google.common.collect.Lists;

import org.openlmis.cce.domain.BaseEntity;
import org.openlmis.cce.dto.BaseDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.cce.util.Message;
import org.openlmis.cce.web.csv.model.ModelClass;
import org.openlmis.cce.web.csv.recordhandler.RecordProcessor;
import org.openlmis.cce.web.csv.recordhandler.RecordWriter;
import org.openlmis.cce.web.validator.CsvHeaderValidator;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;

import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * This class has logic to invoke corresponding respective record handler to parse data from input
 * stream into the corresponding model. To speed up the process for huge files the stream is divided
 * into smaller chunks. The chunk size is set by {@code csvParser.chunkSize} property. Each chunk is
 * executed asynchronously in the thread pool with size set by {@code csvParser.poolSize}.
 */
@Component
@NoArgsConstructor
public class CsvParser {
  private static final XLogger XLOGGER = XLoggerFactory.getXLogger(CsvParser.class);

  @Value("${csvParser.chunkSize}")
  private int chunkSize;

  @Value("${csvParser.poolSize}")
  private int poolSize;

  /**
   * Parses data from input stream into the corresponding model.
   *
   * @return number of uploaded records
   */
  public <D extends BaseDto, E extends BaseEntity> int parse(InputStream inputStream,
                                                             ModelClass<D> modelClass,
                                                             CsvHeaderValidator headerValidator,
                                                             RecordProcessor<D, E> processor,
                                                             RecordWriter<E> writer)
      throws IOException {
    XLOGGER.entry();
    Profiler profiler = new Profiler("PARSE");
    profiler.setLogger(XLOGGER);

    profiler.start("NEW_CSV_BEAN_READER");
    CsvBeanReader<D> csvBeanReader = new CsvBeanReader<>(
        modelClass, inputStream, headerValidator
    );

    profiler.start("VALIDATE_HEADERS");
    csvBeanReader.validateHeaders();

    profiler.start("CREATE_EXECUTOR_SERVICE");
    ExecutorService executor = Executors.newFixedThreadPool(Math.min(1, poolSize));
    List<CompletableFuture<Void>> futures = Lists.newArrayList();

    try {
      profiler.start("HANDLE_FILE");
      while (true) {
        List<D> imported = doRead(csvBeanReader);

        if (imported.isEmpty()) {
          break;
        }

        Runnable runnable = () -> doWrite(processor, writer, imported);
        CompletableFuture<Void> future = runAsync(runnable, executor);
        futures.add(future);
      }
    } finally {
      profiler.start("WAIT_FOR_THREADS");
      futures.forEach(CompletableFuture::join);
    }

    int count = csvBeanReader.getRowNumber() - 1;

    profiler.stop().log();
    XLOGGER.exit(count);

    return count;
  }

  private <D extends BaseDto> List<D> doRead(CsvBeanReader<D> csvBeanReader) throws IOException {
    try {
      List<D> list = Lists.newArrayList();

      for (int i = 0; i < chunkSize; ++i) {
        D imported = csvBeanReader.readWithCellProcessors();

        if (null == imported) {
          break;
        }

        list.add(imported);
      }

      return list;
    } catch (SuperCsvException err) {
      Message message = getCsvRowErrorMessage(err);
      throw new ValidationMessageException(err, message);
    }
  }

  private <D extends BaseDto, E extends BaseEntity> void doWrite(RecordProcessor<D, E> processor,
                                                                 RecordWriter<E> writer,
                                                                 List<D> imported) {
    List<E> entities = imported.stream().map(processor::process).collect(Collectors.toList());
    writer.write(entities);
  }

  private Message getCsvRowErrorMessage(SuperCsvException err) {
    CsvContext context = err.getCsvContext();
    int row = context.getRowNumber() - 1;
    return new Message(ERROR_UPLOAD_RECORD_INVALID, row, err.getMessage());
  }

}
