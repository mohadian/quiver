/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zagros.quiver.rest.core.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.zagros.quiver.rest.core.RestCall;
import com.zagros.quiver.rest.core.RestRequest;
import com.zagros.quiver.rest.core.parser.ParserException;
import com.zagros.quiver.rest.core.parser.RestResponseParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A request for retrieving a {@link RestResponseParser} response body at a
 * given URL, allowing for an optional {@link RestResponseParser} to be passed
 * in as part of the request body.
 */
public class VolleyRestCallRequest extends Request<RestResponseParser> {
    /** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    private final RestCall mRestCall;

    private Priority mPriority;

    private boolean mIgnoreCache;

    /**
     * Creates a new request.
     *
     */
    public VolleyRestCallRequest(final RestCall aRestCall) {
        super(
                (aRestCall.getRequest().getMethod() == com.zagros.quiver.rest.core.RestRequest.Method.GET) ? Request.Method.GET
                        : Request.Method.POST, aRestCall.getRequest().getUrl(),
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        aRestCall.getListener().onFailure(aRestCall,
                                new VolleyError("Error initializing the request"));
                    }
                });
        mRestCall = aRestCall;
        mRestCall.getListener().onProgressStarted(mRestCall);
    }

    protected void finish(String tag) {
        //super.finish(tag);
        mRestCall.getListener().onProgressFinished(mRestCall);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        parseErrorResponse(volleyError);
        return volleyError;
    }

    private void parseErrorResponse(VolleyError volleyError) {
        RestResponseParser jsonParser = mRestCall.getParser();
        jsonParser.setError(volleyError);
        try
        {
            if (volleyError.networkResponse != null) {
                jsonParser.setResponseContent(volleyError.networkResponse);
                jsonParser.parseError();
                if (jsonParser.getError() != null) {
                    VolleyLog.d("Subscription Response jsonParser.getErrorResult() != null"
                            + jsonParser.getError());
                } else {
                    VolleyLog.d("Subscription ResponsejsonParser.getErrorResult() == null");
                }
            }

        } catch (IllegalStateException e) {
            VolleyLog.e("IllegalStateException", e);
            if (jsonParser != null) {
                jsonParser.setError(
                        new VolleyError(" HttpURLConnection.HTTP_UNSUPPORTED_TYPE", e));
            }

            VolleyLog.e("Subscription Response IllegalStateException ", e);
        } catch (ParserException e) {
            VolleyLog.e("Exception", e);
            if (jsonParser != null) {
                jsonParser.setError(
                        new VolleyError(" HttpURLConnection.HTTP_UNSUPPORTED_TYPE", e));
            }
            VolleyLog.e("Subscription Response ParserException ", e);
        }
    }

    @Override
    protected Response<RestResponseParser> parseNetworkResponse(NetworkResponse response) {
        RestResponseParser jsonParser = mRestCall.getParser();
        try {
            jsonParser.setResponseContent(response);
            jsonParser.parse();
            if (isIgnoreCache()) {
                return Response.success(jsonParser, null);
            } else {
                return Response.success(jsonParser,
                        HttpHeaderParser.parseCacheHeaders(response));
            }

        } catch (IllegalStateException e) {
            VolleyLog.e("IllegalStateException", e);
            return Response.error(new ParseError(response));
        } catch (ParserException e) {
            return Response.error(new ParseError(response));
        }

    }

    @Override
    protected void deliverResponse(RestResponseParser response) {
        if (response.isSuccessful()) {
            mRestCall.getListener().onSuccess(mRestCall);
        } else {
            mRestCall.getListener().onFailure(mRestCall, response.getError());
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        parseErrorResponse(error);
        mRestCall.getListener().onFailure(mRestCall, error);
    }

    @Override
    public byte[] getBody() {
        try {
            return mRestCall.getRequest().getContentBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of request using %s",
                    PROTOCOL_CHARSET);
            return null;
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        List<RestRequest.Header> list = mRestCall.getRequest().getHeaderList();
        Map<String, String> headers = new HashMap<String, String>();
        for (Iterator<RestRequest.Header> iterator = list.iterator(); iterator.hasNext();) {
            RestRequest.Header object = (RestRequest.Header) iterator.next();
            headers.put(object.getName(), object.getValue());
        }
        return headers;
    }

    @Override
    public String getBodyContentType() {
        String ct = mRestCall.getRequest().getContentType();
        if (ct != null) {
            return ct;
        } else {
            return super.getBodyContentType();
        }
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    public Priority getPriority() {
        if (mPriority == null) {
            return super.getPriority();
        } else {
            return mPriority;
        }
    }

    public boolean isIgnoreCache() {
        return mIgnoreCache;
    }

    public void setIgnoreCache(boolean mIgnoreCache) {
        this.mIgnoreCache = mIgnoreCache;
    }
}
