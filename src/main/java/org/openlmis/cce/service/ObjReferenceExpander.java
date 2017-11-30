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

package org.openlmis.cce.service;

import static org.openlmis.cce.i18n.MessageKeys.ERROR_DTO_EXPANSION_ASSIGNMENT;
import static org.openlmis.cce.i18n.MessageKeys.ERROR_DTO_EXPANSION_CAST;
import static org.openlmis.cce.i18n.MessageKeys.ERROR_DTO_EXPANSION_HREF;
import static org.openlmis.cce.util.RequestHelper.createEntity;
import static org.openlmis.cce.util.RequestHelper.createUri;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.cce.dto.ObjectReferenceDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.openlmis.util.converter.UuidConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

@Component
public class ObjReferenceExpander {

  private static final Logger LOGGER = LoggerFactory.getLogger(ObjReferenceExpander.class);

  @Autowired
  private AuthService authService;

  private BeanUtilsBean beanUtils;

  private RestOperations restTemplate = new RestTemplate();

  /**
   * Create an instance of the {@link BeanUtilsBean} and register custom converters with it.
   */
  @PostConstruct
  public void registerConverters() {
    beanUtils = BeanUtilsBean.getInstance();
    beanUtils.getConvertUtils().register(new UuidConverter(), UUID.class);
  }

  /**
   * Expands the DTO object. The requirement is that the field names in the {@code expands}
   * list exactly correspond to the field names in the passed DTO object. Moreover, those fields
   * need to extend the {@link ObjectReferenceDto}. If that's the case, this method will query the
   * URL from {@link ObjectReferenceDto#getHref()} and add the retrieved fields to the
   * representation.
   *
   * @param dto the DTO to expand
   * @param expands a list of field names from the passed DTO to expand
   */
  public void expandDto(Object dto, List<String> expands) {
    if (expands == null) {
      return;
    }

    for (String expand : expands) {
      try {
        ObjectReferenceDto refDto = getObjectReferenceDto(dto, expand);
        String href = getHref(expand, refDto);

        Map<String, Object> refObj = retrieve(href);
        if (MapUtils.isNotEmpty(refObj)) {
          beanUtils.populate(refDto, refObj);
        }
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
        throw new ValidationMessageException(ex, ERROR_DTO_EXPANSION_ASSIGNMENT, expand);
      }
    }
  }

  private String getHref(String expand, ObjectReferenceDto refDto) {
    String href = refDto.getHref();
    if (StringUtils.isBlank(href)) {
      throw new ValidationMessageException(ERROR_DTO_EXPANSION_HREF, expand);
    }
    return href;
  }

  private ObjectReferenceDto getObjectReferenceDto(Object dto, String expand)
      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Object retrievedField = PropertyUtils.getProperty(dto, expand);

    if (!(retrievedField instanceof ObjectReferenceDto)) {
      throw new ValidationMessageException(ERROR_DTO_EXPANSION_CAST, expand);
    }
    return (ObjectReferenceDto) retrievedField;
  }

  private Map<String, Object> retrieve(String href) {
    HttpEntity<Object> entity = createEntity(null,
        RequestHeaders.init().setAuth(authService.obtainAccessToken()));
    try {
      return restTemplate.exchange(createUri(href), HttpMethod.GET, entity, Map.class).getBody();
    } catch (HttpStatusCodeException ex) {
      // We don't want to stop processing if the referenced instance does not exist.
      if (HttpStatus.NOT_FOUND == ex.getStatusCode()) {
        LOGGER.warn("The instance referenced under {} does not exist.", href);
        return null;
      }

      throw DataRetrievalException.build(href, ex);
    }
  }

}
