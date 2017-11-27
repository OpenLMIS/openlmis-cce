package org.openlmis.cce.service;

import org.apache.commons.beanutils.Converter;

import java.util.UUID;

public class UuidConverter implements Converter {

  @Override
  public <T> T convert(Class<T> type, Object value) {
    if (value == null || value.toString().isEmpty()) {
      return null;
    }

    return (T) UUID.fromString(value.toString().trim());
  }
}
