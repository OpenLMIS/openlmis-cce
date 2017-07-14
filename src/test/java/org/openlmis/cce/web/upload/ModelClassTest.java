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

package org.openlmis.cce.web.upload;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.openlmis.cce.web.upload.processor.CsvCellProcessors.STRING_TYPE;

import org.junit.Test;
import org.openlmis.cce.web.dummy.DummyObject;
import java.util.Arrays;
import java.util.List;


public class ModelClassTest {

  @Test
  public void shouldGetFieldNameMappingsGivenTheHeader() {
    List<String> headers =
        Arrays.asList("mandatory string field", "mandatoryIntField", "OPTIONAL NESTED FIELD");

    ModelClass modelClass = new ModelClass(DummyObject.class);
    final String[] mappings = modelClass.getFieldNameMappings(
        headers.toArray(new String[headers.size()]));
    assertThat(mappings[0], is("mandatoryStringField"));
    assertThat(mappings[1], is("mandatoryIntField"));
    assertThat(mappings[2], is("dummyNestedField.code"));
  }

  @Test
  public void shouldFindImportFieldWithName() {
    ModelClass modelClass = new ModelClass(DummyObject.class);
    ModelField importFieldWithName = modelClass.findImportFieldWithName("Mandatory String Field");

    assertEquals(importFieldWithName.getName(), "Mandatory String Field");
    assertEquals(importFieldWithName.getType(), STRING_TYPE);
  }

}