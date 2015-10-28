
package com.zagros.quiver.rest.core.parser;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

public abstract class RestResponseParser<T, E> {
    protected boolean mIsSuccessful = false;
    protected VolleyError mErrorResult;
    protected NetworkResponse mResponse;
    protected T mRestResponse;
    protected E mRestAltResponse;

    public RestResponseParser() {
    }

    public RestResponseParser(T aRestResponse) {
        mRestResponse = aRestResponse;
    }

    public abstract void setResponseContent(NetworkResponse response) throws ParserException;

    public abstract void parse() throws ParserException;

    public abstract void parseError() throws ParserException;

    public boolean isSuccessful() {
        return mIsSuccessful;
    }

    public void setIsSuccessful(boolean isSuccessful) {
        mIsSuccessful = isSuccessful;
    }

    public VolleyError getError() {
        return mErrorResult;
    }

    public void setError(VolleyError aVolleyError) {
        mErrorResult = aVolleyError;
    }

    public T getRestObject() {
        return mRestResponse;
    }

    public void setRestObject(T aRestResponse) {
        mRestResponse = aRestResponse;
    }

    public void setAltRestObject(E aRestResponse) {
        mRestAltResponse = aRestResponse;
    }

}
