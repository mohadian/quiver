
package com.zagros.quiver.rest.core.builder.content;

import org.apache.http.HttpEntity;

import java.io.UnsupportedEncodingException;

/**
 * Interface for building network API request. All request content builders
 * return prepared HttpEntity for passing inside HttpRequestBase (base class for
 * all HTTP method).
 */
public interface RequestContentBuilder {
    HttpEntity getEntity();

    byte[] getContentBytes(String protocolCharset) throws UnsupportedEncodingException;
}
