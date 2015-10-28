
package com.zagros.quiver.rest.core;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Helper class for building HttpUriRequests based on the Request HTTP method
 */
public class RequestBuilder {

    /**
     * Returns the HttpUriRequests based on the Request HTTP method
     *
     * @param request
     * @return
     * @throws RequestBuilderException
     */
    public static HttpUriRequest getInitializedHttpRequest(RestRequest request)
            throws RequestBuilderException {
        HttpRequestBase requestBase = null;
        RestRequest.Method httpMethod = request.getMethod();
        switch (httpMethod) {
            case DELETE:
                requestBase = new HttpDelete();
                break;
            case GET:
                requestBase = new HttpGet();
                break;
            case POST:
                requestBase = new HttpPost();
                ((HttpPost) requestBase).setEntity(request.getContentEntity());
                break;
            case PUT:
                requestBase = new HttpPut();
                ((HttpPut) requestBase).setEntity(request.getContentEntity());
                break;
            default:
                // requests with these methods should never be seen here
                // Request.Method.ALL
                // Request.Method.NOT_DEFINED
                throw new RequestBuilderException("Unsupported HTTP Method");
        }
        try {
            // add headers to final request
            List<RestRequest.Header> headerList = request.getHeaderList();
            for (RestRequest.Header header : headerList) {
                requestBase.addHeader(new BasicHeader(header.getName(), header.getValue()));
            }

            requestBase.setURI(new URI(request.getUrl()));
        } catch (URISyntaxException e) {
            throw new RequestBuilderException("Invalid URI Syntax", e);
        }
        return (HttpUriRequest) requestBase;
    }
}
