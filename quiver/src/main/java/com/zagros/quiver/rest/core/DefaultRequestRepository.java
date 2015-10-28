
package com.zagros.quiver.rest.core;

import com.zagros.quiver.rest.core.builder.url.RequestUrlSpec;
import com.zagros.quiver.rest.core.builder.url.UrlBuilder;
import com.zagros.quiver.rest.core.builder.url.UrlBuilderException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @param <E>
 * @author Mostafa.Hadian
 */
public class DefaultRequestRepository<E extends Enum<E>> implements RequestRepository<E> {

    private class RequestSpec {
        public RestRequest.Method httpMethod;
        public RequestUrlSpec urlSpec;
        public boolean isAnonymous;
        public List<RestRequest.Header> headerList;
        public String contentType;

        public RequestSpec(RestRequest.Method httpMethod, RequestUrlSpec urlSpec) {
            this(httpMethod, urlSpec, new ArrayList<RestRequest.Header>());
        }

        public RequestSpec(RestRequest.Method httpMethod, RequestUrlSpec urlSpec,
                           List<RestRequest.Header> headerList) {
            this(httpMethod, urlSpec, headerList, true, null);
        }

        public RequestSpec(RestRequest.Method httpMethod, RequestUrlSpec urlSpec,
                           List<RestRequest.Header> headerList, String contentType) {
            this(httpMethod, urlSpec, headerList, true, contentType);
        }

        public RequestSpec(RestRequest.Method httpMethod, RequestUrlSpec urlSpec,
                           List<RestRequest.Header> headerList, boolean isAnonymous, String contentType) {
            this.httpMethod = httpMethod;
            this.urlSpec = urlSpec;
            this.headerList = headerList;
            this.isAnonymous = isAnonymous;
            this.contentType = contentType;
        }
    }

    public DefaultRequestRepository(ServiceApi<E> serviceApi, Class<E> clazz) {
        mServiceApi = serviceApi;
        mClass = clazz;
        mRequestSpecMap = new EnumMap<E, RequestSpec>(clazz);
        addAllRequestSpecs();
    }

    protected void addAllRequestSpecs() {
        for (ServiceApi.Call<E> call : mServiceApi.getCallList()) {
            mRequestSpecMap.put(call.callKey, new RequestSpec(call.httpMethod, call.urlSpec,
                    call.headerList, call.mIsAnonymous, call.contentType));
        }
    }

    public void addRequestSpec(E specKey, RestRequest.Method method, RequestUrlSpec urlSpec) {
        addRequestSpec(specKey, new RequestSpec(method, urlSpec));
    }

    private void addRequestSpec(E specKey, RequestSpec requestSpec) {
        mRequestSpecMap.put(specKey, requestSpec);
    }

    /**
     * Builds Request for specKey. With given args.
     *
     * @param specKey
     * @param args
     * @return
     * @throws RestException
     */
    public RestRequest buildRequest(E specKey, String... args) throws RestException {
        if (!mRequestSpecMap.containsKey(specKey)) {
            throw new RestException("Request key:'" + specKey + "' not found");
        }

        RequestSpec requestSpec = mRequestSpecMap.get(specKey);
        String requestUrl;
        RestRequest request = null;
        try {
            requestUrl = UrlBuilder.getInstance().buildUrl(requestSpec.urlSpec, args);
            request = new RestRequest(requestUrl, requestSpec.urlSpec, requestSpec.httpMethod,
                    requestSpec.isAnonymous, requestSpec.contentType);
            addHeadersTo(request, requestSpec);
        } catch (UrlBuilderException e) {
            throw new RestException("Building request failed", e);
        }

        return request;
    }

    /**
     * Adds headers to request. Headers are added in order: first ALL headers
     * are added and then headers for the HTTP method.
     *
     * @param request - Request to whom the headers will be added
     */
    protected void addHeadersTo(RestRequest request, RequestSpec requestSpec) {
        List<RestRequest.Header> headersToAdd = getMergedDefaultHeadersFor(request);
        headersToAdd = mergeInRequestSpecificHeaders(requestSpec.headerList, headersToAdd);
        request.addHeaderList(headersToAdd);
    }

    protected List<RestRequest.Header> getMergedDefaultHeadersFor(RestRequest request) {
        List<RestRequest.Header> headerList = new ArrayList<RestRequest.Header>();
        headerList.addAll(mServiceApi.getHeaderList(RestRequest.Method.ALL));
        headerList.addAll(mServiceApi.getHeaderList(request.getMethod()));
        return headerList;
    }

    protected Map<String, RestRequest.Header> buildHeaderMap(List<RestRequest.Header> headerList) {
        Map<String, RestRequest.Header> headerMap = new HashMap<String, RestRequest.Header>();
        for (RestRequest.Header header : headerList) {
            headerMap.put(header.getName(), header);
        }
        return headerMap;
    }

    protected List<RestRequest.Header> mergeInRequestSpecificHeaders(
            List<RestRequest.Header> requestHeaderList, List<RestRequest.Header> defaultHeaderList) {
        Map<String, RestRequest.Header> headerMap = buildHeaderMap(defaultHeaderList);

        for (RestRequest.Header header : requestHeaderList) {
            if (header.isReplaceable() && headerMap.containsKey(header.getName())) {
                // remove header from list and map, and add new one instead
                // of just changing value, or it will introduce bug as the
                // header will stay the same for the next request build
                RestRequest.Header headerToReplace = headerMap.remove(header.getName());
                defaultHeaderList.remove(headerToReplace);
                defaultHeaderList.add(header);
                headerMap.put(header.getName(), header);
            } else if (header.isDeleteable() && headerMap.containsKey(header.getName())) {
                RestRequest.Header headerToDelete = headerMap.remove(header.getName());
                defaultHeaderList.remove(headerToDelete);
            }
            // new header, is not replaceable and no deleteable and
            // does not exist already
            else if (!header.isDeleteable() && !header.isReplaceable()
                    && !headerMap.containsKey(header.getName())) {
                headerMap.put(header.getName(), header);
                defaultHeaderList.add(header);
            }
        }
        return defaultHeaderList;
    }

    private final ServiceApi<E> mServiceApi;
    private final Map<E, RequestSpec> mRequestSpecMap;
    private final Class<E> mClass;
}
