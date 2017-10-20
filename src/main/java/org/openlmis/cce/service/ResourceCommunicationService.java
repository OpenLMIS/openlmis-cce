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

import org.openlmis.cce.util.DynamicPageTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ResourceCommunicationService<T> extends BaseCommunicationService<T> {

  /**
   * Return one object from service.
   *
   * @param id UUID of requesting object.
   * @return Requesting reference data object.
   */
  public T findOne(UUID id) {
    try {
      return execute(id.toString(), getResultClass()).getBody();
    } catch (HttpStatusCodeException ex) {
      // rest template will handle 404 as an exception, instead of returning null
      if (HttpStatus.NOT_FOUND == ex.getStatusCode()) {
        logger.warn(
            "{} matching params does not exist.",
            getResultClass().getSimpleName()
        );

        return null;
      }

      throw buildDataRetrievalException(ex);
    }
  }

  public List<T> findAll() {
    return findAll("", getArrayResultClass());
  }

  /**
   * Return all reference data T objects.
   *
   * @param resourceUrl Endpoint url.
   * @param parameters  Map of query parameters.
   * @return all reference data T objects.
   */
  public List<T> findAll(String resourceUrl, RequestParameters parameters) {
    return findAll(resourceUrl, parameters, null, HttpMethod.GET, getArrayResultClass());
  }

  protected <P> List<P> findAll(String resourceUrl, Class<P[]> type) {
    return findAll(resourceUrl, RequestParameters.init(), null, HttpMethod.GET, type);
  }

  protected <P> List<P> findAll(String resourceUrl, RequestParameters parameters,
                                Object payload, HttpMethod method, Class<P[]> type) {
    try {
      return Stream
          .of(execute(resourceUrl, parameters, payload, method, type).getBody())
          .collect(Collectors.toList());
    } catch (HttpStatusCodeException ex) {
      throw buildDataRetrievalException(ex);
    }
  }

  protected <P> ServiceResponse<List<P>> tryFindAll(String resourceUrl, Class<P[]> type,
                                                    String etag) {
    try {
      RequestHeaders headers = RequestHeaders.init().setIfNoneMatch(etag);
      ResponseEntity<P[]> response = execute(resourceUrl, null, headers, HttpMethod.GET, type);

      if (response.getStatusCode() == HttpStatus.NOT_MODIFIED) {
        return new ServiceResponse<>(null, response.getHeaders(), false);
      } else {
        List<P> list = Stream.of(response.getBody()).collect(Collectors.toList());
        return new ServiceResponse<>(list, response.getHeaders(), true);
      }
    } catch (HttpStatusCodeException ex) {
      throw buildDataRetrievalException(ex);
    }
  }

  /**
   * Return all reference data T objects for Page that need to be retrieved with POST request.
   *
   * @param resourceUrl Endpoint url.
   * @param parameters  Map of query parameters.
   * @param payload     body to include with the outgoing request.
   * @return Page of reference data T objects.
   */
  public Page<T> getPage(String resourceUrl, RequestParameters parameters, Object payload) {
    try {
      DynamicPageTypeReference<T> type = new DynamicPageTypeReference<>(getResultClass());
      return execute(resourceUrl, parameters, payload, HttpMethod.POST, type).getBody();
    } catch (HttpStatusCodeException ex) {
      throw buildDataRetrievalException(ex);
    }
  }

  private DataRetrievalException buildDataRetrievalException(HttpStatusCodeException ex) {
    return new DataRetrievalException(getResultClass().getSimpleName(),
        ex.getStatusCode(),
        ex.getResponseBodyAsString());
  }

}
