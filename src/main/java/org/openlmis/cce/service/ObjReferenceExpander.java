package org.openlmis.cce.service;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.cce.dto.ObjectReferenceDto;
import org.openlmis.cce.exception.ValidationMessageException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

@Service
public class ObjReferenceExpander extends BaseCommunicationService {

  private BeanUtilsBean beanUtils;

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
        ObjectReferenceDto refDto = (ObjectReferenceDto) PropertyUtils.getProperty(dto, expand);

        String href = refDto.getHref();
        Map<String, Object> refObj = retrieve(href);
        beanUtils.populate(refDto, refObj);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
        throw new ValidationMessageException("Can not expand DTO", ex);
      }
    }
  }

  private Map<String, Object> retrieve(String href) {
    return execute(href, null, null, null, HttpMethod.GET, Map.class).getBody();
  }

  @Override
  protected String getServiceUrl() {
    return StringUtils.EMPTY;
  }

  @Override
  protected String getUrl() {
    return StringUtils.EMPTY;
  }
}
