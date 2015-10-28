
package com.zagros.quiver.rest.core;

import com.zagros.quiver.rest.core.builder.content.RequestContentBuilder;
import com.zagros.quiver.rest.core.builder.url.RequestUrlSpec;

import org.apache.http.HttpEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the API request object
 *
 * @author Mostafa.Hadian
 */
public class RestRequest {
    private final Method mMethod;
    private RequestContentBuilder mContentBuilder;
    private RequestPriority mPriority;

    private final RequestUrlSpec mUrlSpec;
    private final String mUrl;

    private final String mRequsetId;

    private boolean mIsAnonymous;
    private final String mContentType;

    private final List<Header> mHeaderList;
    private final int HEADER_LIST_SIZE = 10;

    /**
     * Creates an anonymous API request
     *
     * @param url
     * @param urlSpec
     * @param method
     */
    public RestRequest(String url, RequestUrlSpec urlSpec, Method method) {
        this(url, urlSpec, method, true);
    }

    /**
     * Creates an API request
     *
     * @param url
     * @param urlSpec
     * @param method
     * @param isAnonymous
     */
    public RestRequest(String url, RequestUrlSpec urlSpec, Method method,
                       boolean isAnonymous) {
        this(url, urlSpec, method, isAnonymous, null);
    }

    /**
     * Creates an API request
     *
     * @param url
     * @param urlSpec
     * @param method
     * @param isAnonymous
     * @param contentType
     */
    public RestRequest(String url, RequestUrlSpec urlSpec, Method method,
                       boolean isAnonymous, String contentType) {
        mUrl = url;
        mUrlSpec = urlSpec;
        mMethod = method;
        mHeaderList = new ArrayList<Header>(HEADER_LIST_SIZE);
        mRequsetId = generateRequestId();
        mPriority = RequestPriority.NORMAL;
        mIsAnonymous = isAnonymous;
        mContentType = contentType;
    }

    /**
     * Returns the HttpEntity which is created by the corresponding
     * ContentBuilder
     *
     * @return
     */
    public HttpEntity getContentEntity() {
        if (getContentBuilder() != null) {
            return getContentBuilder().getEntity();
        }
        return null;
    }

    /**
     * Returns the HttpEntity which is created by the corresponding
     * ContentBuilder
     *
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public byte[] getContentBytes(String protocolCharset) throws UnsupportedEncodingException {
        if (getContentBuilder() != null) {
            return getContentBuilder().getContentBytes(protocolCharset);
        }
        return null;
    }

    /**
     * Add a Http header
     *
     * @param header
     */
    public void addHeader(Header header) {
        mHeaderList.add(header);
    }

    /**
     * Removes the Http header
     *
     * @param name
     */
    public void removeHeader(String name) {
        for (Header header : mHeaderList) {
            if (header.getName().equalsIgnoreCase(name)) {
                mHeaderList.remove(header);
                break;
            }
        }
    }

    /**
     * Add a list of Http header
     *
     * @param headerList
     */
    public void addHeaderList(List<Header> headerList) {
        for (Header header : headerList) {
            mHeaderList.add(header);
        }
    }

    /**
     * Returns the list of Http headers assigned to the request object
     *
     * @return
     */
    public List<Header> getHeaderList() {
        return mHeaderList;
    }

    /**
     * Returns the Http Method assigned to the request object
     *
     * @return
     */
    public Method getMethod() {
        return mMethod;
    }

    /**
     * Returns the URL assigned to the request object
     *
     * @return
     */
    public String getUrl() {
        return mUrl;
    }

    public String getRequestId() {
        return mRequsetId;
    }

    private String generateRequestId() {
        StringBuilder sb = new StringBuilder();
        sb.append("APICALL").append("_").append(System.currentTimeMillis());
        return sb.toString();
    }

    /**
     * Returns the priority level of the request
     *
     * @return
     */
    public RequestPriority getPriority() {
        return mPriority;
    }

    /**
     * Sets the priority level of the request
     *
     * @param priority
     */
    public void setPriority(RequestPriority priority) {
        mPriority = priority;
    }

    /**
     * Checks whether if it is an anonymous request or not. If it is not an
     * anonymous request we need to add the Access Token to the request body
     *
     * @return
     */
    public boolean isAnonymous() {
        return mIsAnonymous;
    }

    public void setIsAnonymous(boolean aIsAnonymous) {
        mIsAnonymous = aIsAnonymous;
    }

    public RequestContentBuilder getContentBuilder() {
        return mContentBuilder;
    }

    public void setContentBuilder(RequestContentBuilder aContentBuilder) {
        mContentBuilder = aContentBuilder;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Request: Method ");
        sb.append(getMethod());
        sb.append(" Is ananymous ");
        sb.append(isAnonymous());
        sb.append(" Content type ");
        sb.append(getContentType());

        return sb.toString();
    }

    public String getContentType() {
        return mContentType;
    }

    /**
     * Request HTTP Method
     *
     * @author Mostafa.Hadian
     */
    public enum Method {
        NOT_DEFINED,
        ALL,
        GET,
        POST,
        PUT,
        DELETE
    }

    /**
     * Request priority
     *
     * @author Mostafa.Hadian
     */
    public enum RequestPriority {
        LOW(0),
        NORMAL(50),
        HIGH(100),
        HIGHEST(150);

        private RequestPriority(int value) {
            mValue = value;
        }

        // Converts from an ordinal value to the RequestPriority
        public static RequestPriority valueOf(int index) {
            RequestPriority[] values = RequestPriority.values();
            if (index < 0 || index >= values.length) {
                return LOW;
            }
            return values[index];
        }

        private int mValue = 0;
    }

    /**
     * Request Http Headers
     *
     * @author Mostafa.Hadian
     */
    public static class Header {
        private final String mName;
        private String mValue;

        protected boolean mIsDeleteable;
        protected boolean mIsReplaceable;

        public Header(String name, String value) {
            mName = name;
            mValue = value;
            mIsDeleteable = false;
            mIsReplaceable = false;
        }

        public Header deleteable(boolean isDeletable) {
            mIsDeleteable = isDeletable;
            return this;
        }

        public Header replaceable(boolean isReplaceable) {
            mIsReplaceable = isReplaceable;
            return this;
        }

        public boolean isReplaceable() {
            return mIsReplaceable;
        }

        public boolean isDeleteable() {
            return mIsDeleteable;
        }

        public String getValue() {
            return mValue;
        }

        public Header setValue(String value) {
            mValue = value;
            return this;
        }

        public String getName() {
            return mName;
        }
    }
}
