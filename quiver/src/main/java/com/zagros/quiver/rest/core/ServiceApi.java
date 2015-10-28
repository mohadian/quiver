
package com.zagros.quiver.rest.core;

import com.zagros.quiver.rest.core.builder.url.RequestUrlSpec;
import com.zagros.quiver.rest.core.builder.url.strategy.UrlBuildStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ServiceApi<E extends Enum<E>> {
    private List<Call<E>> mCallList;
    private String mRootUrl = "";
    protected UrlBuildStrategy mUrlBuildStrategy = null;
    private Map<RestRequest.Method, List<RestRequest.Header>> mHeaderMap;

    public ServiceApi(String rootUrl, UrlBuildStrategy urlBuildStrategy) {
        mRootUrl = rootUrl;
        mUrlBuildStrategy = urlBuildStrategy;
        mCallList = new LinkedList<Call<E>>();
        mHeaderMap = new HashMap<RestRequest.Method, List<RestRequest.Header>>();
        init();
    }

    public ServiceApi(String rootUrl) {
        mRootUrl = rootUrl;
        mCallList = new LinkedList<Call<E>>();
        mUrlBuildStrategy = null;
        mHeaderMap = new HashMap<RestRequest.Method, List<RestRequest.Header>>();
        init();
    }

    /**
     * All actual method definitions for particular API
     */
    protected abstract void initMethods();

    /**
     * All actual method definitions for particular API
     */
    protected abstract void addToRepository();

    /**
     * Sets up default headers used for HTTP methods in this API
     */
    protected abstract void defaultHeaders();

    protected void init() {
        defaultHeaders();
        initMethods();
        addToRepository();
    }

    public void setRootUrl(String baseUrl) {
        mRootUrl = baseUrl;
    }

    public String getRootUrl() {
        return mRootUrl;
    }

    public void addDefaultHeader(RestRequest.Method method, String headerName, String headerValue) {
        getHeaderList(method).add(new RestRequest.Header(headerName, headerValue));
    }

    public List<RestRequest.Header> getHeaderList(RestRequest.Method method) {
        if (!mHeaderMap.containsKey(method)) {
            List<RestRequest.Header> headerList = new ArrayList<RestRequest.Header>();
            mHeaderMap.put(method, headerList);
        }
        return mHeaderMap.get(method);
    }

    public List<Call<E>> getCallList() {
        return mCallList;
    }

    /**
     * Adds API call to the list of API calls. If there is explicit root url
     * and/or build strategy set in call object, then don't override these
     * parameters. If not, override with defaults.
     *
     * @param call
     */
    protected void addApiCall(Call<E> call) {
        RequestUrlSpec urlSpec = call.urlSpec;

        if (!urlSpec.isRootUrlSet()) {
            urlSpec.setRootUrl(mRootUrl);
        }
        if (!urlSpec.isBuildStrategySet()) {
            urlSpec.setBuildStrategy(mUrlBuildStrategy);
        }

        mCallList.add(call);
    }

    public static class Call<E extends Enum<E>> {
        public E callKey;
        public RestRequest.Method httpMethod;
        public RequestUrlSpec urlSpec;
        public List<RestRequest.Header> headerList;
        public String contentType;

        public boolean mIsAnonymous;

        public boolean isAnonymous() {
            return mIsAnonymous;
        }

        public Call<E> setIsAnonymous(boolean aIsAnonymous) {
            mIsAnonymous = aIsAnonymous;
            return this;
        }

        public String getContentType() {
            return contentType;
        }

        public Call<E> setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Call() {
            headerList = new ArrayList<RestRequest.Header>();
        }

        public Call<E> setKey(E key) {
            callKey = key;
            return this;
        }

        public Call<E> setMethod(RestRequest.Method method) {
            httpMethod = method;
            return this;
        }

        public Call<E> setUrlSpec(RequestUrlSpec spec) {
            urlSpec = spec;
            return this;
        }

        public RequestUrlSpec getUrlSpec() {
            return urlSpec;
        }

        public Call<E> addHeader(String name, String value) {
            headerList.add(new RestRequest.Header(name, value));
            return this;
        }

    }
}
