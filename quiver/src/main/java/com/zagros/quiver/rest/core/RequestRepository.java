
package com.zagros.quiver.rest.core;

import com.zagros.quiver.rest.core.builder.url.RequestUrlSpec;

/**
 * An abstraction class for Request Repository object.
 *
 * @param <T>
 * @author Mostafa.Hadian
 */
public interface RequestRepository<T> {
    void addRequestSpec(T specKey, RestRequest.Method method, RequestUrlSpec urlSpec);

    RestRequest buildRequest(T specKey, String... args) throws RestException;
}
