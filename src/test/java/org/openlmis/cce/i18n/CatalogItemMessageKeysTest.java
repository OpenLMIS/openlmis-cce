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

package org.openlmis.cce.i18n;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;

/**
 * The service sets useCodeAsDefaultMessage, so a key with no entry in the bundle localizes to the
 * key itself and the usual assertion of a localized message against the same MessageService passes
 * whether or not the text exists. This test reads the bundle instead.
 */
public class CatalogItemMessageKeysTest {

  private Properties messages;

  @Before
  public void setUp() throws IOException {
    messages = new Properties();
    try (InputStream stream = getClass().getClassLoader()
        .getResourceAsStream("messages_en.properties")) {
      messages.load(stream);
    }
  }

  @Test
  public void everyCatalogItemMessageKeyShouldHaveEnglishText() throws IllegalAccessException {
    List<String> missing = new ArrayList<>();

    for (Field field : CatalogItemMessageKeys.class.getDeclaredFields()) {
      if (!isPublicConstant(field) || !String.class.equals(field.getType())) {
        continue;
      }

      String key = (String) field.get(null);

      if (!messages.containsKey(key)) {
        missing.add(field.getName() + " (" + key + ")");
      }
    }

    assertTrue("messages_en.properties has no text for: " + missing, missing.isEmpty());
  }

  private boolean isPublicConstant(Field field) {
    int modifiers = field.getModifiers();
    return Modifier.isPublic(modifiers)
        && Modifier.isStatic(modifiers)
        && Modifier.isFinal(modifiers);
  }
}
