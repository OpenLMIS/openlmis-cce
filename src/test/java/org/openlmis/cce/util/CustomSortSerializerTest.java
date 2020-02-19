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

package org.openlmis.cce.util;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.springframework.data.domain.Sort;

public class CustomSortSerializerTest {

  @Test
  public void shouldSerializeSortToJson() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    ObjectNode order = mapper.createObjectNode();
    order.put("direction", "DESC");
    order.put("property", "startDate");
    order.put("ignoreCase", false);
    order.put("nullHandling", "NATIVE");
    order.put("ascending", false);
    order.put("descending", true);

    ArrayNode arrayNode = mapper.createArrayNode();
    arrayNode.add(order);

    Sort.Order[] orders = new Sort.Order[arrayNode.size()];
    Sort sort = Sort.by(orders);

    TestObject testObject = new TestObject();
    testObject.setSort(sort);

    String serializedSort = serialize(testObject.getSort());

    assertTrue(serializedSort.contains("\"sorted\":true"));
  }

  private String serialize(Sort sort) throws IOException {
    return new ObjectMapper().writeValueAsString(sort);
  }

  @AllArgsConstructor
  @NoArgsConstructor
  private static class TestObject {

    private Sort sort;

    public Sort getSort() {
      return sort;
    }

    @JsonSerialize(using = CustomSortSerializer.class)
    public void setSort(Sort sort) {
      this.sort = sort;
    }
  }
}
