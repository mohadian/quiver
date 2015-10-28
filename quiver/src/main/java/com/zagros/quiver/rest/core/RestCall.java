
package com.zagros.quiver.rest.core;

import com.zagros.quiver.rest.core.listeners.RestResponseListener;
import com.zagros.quiver.rest.core.parser.RestResponseParser;

/***
 * Class that is used as request/response wrapper
 */
public class RestCall<E extends Enum<E>> {
    private String mCallId;
    private RestRequest mRequest;
    private RestResponseParser mParser;
    private final RestResponseListener mListener;
    private E mRemoteMethod;

    public RestCall() {
        this(null, null, null, null);
    }

    public RestCall(Object owner, RestRequest request, RestResponseParser parser,
                    RestResponseListener listener) {
        mCallId = generateCallId();
        mRequest = request;
        mParser = parser;
        mListener = listener;
    }

    public String getCallId() {
        return mCallId;
    }

    public String generateCallId() {
        return "RESTCALL_" + System.currentTimeMillis();
    }

    public void setRequest(RestRequest request) {
        mRequest = request;
    }

    public RestRequest getRequest() {
        return mRequest;
    }

    public RestResponseParser getParser() {
        return mParser;
    }

    public void setParser(RestResponseParser parser) {
        mParser = parser;
    }

    public RestResponseListener getListener() {
        return mListener;
    }

    public RestCall<E> setRemoteMethod(E method) {
        mRemoteMethod = method;
        return this;
    }

    public E getRemoteMethod() {
        return mRemoteMethod;
    }

    public boolean isOwnedBy(Object aOwner) {
        return false;// mOwner.equals(aOwner);
    }

    public Object getOwner() {
        // return mOwner;
        return null;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Rest Call: ");
        sb.append(mRequest.toString());
        return sb.toString();
    }
}
