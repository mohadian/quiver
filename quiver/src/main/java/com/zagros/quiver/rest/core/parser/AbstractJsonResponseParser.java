
package com.zagros.quiver.rest.core.parser;

import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Default implementation for Json parser
 */
public class AbstractJsonResponseParser extends RestResponseParser<JSONObject, JSONArray> {

    public AbstractJsonResponseParser() {

    }

    @Override
    public void parse() throws ParserException {
        if (mRestResponse == null && mRestAltResponse == null) {
            throw new ParserException("The response need to be initialized");
        }
    }

    @Override
    public void parseError() throws ParserException {
        if (mRestResponse == null && mRestAltResponse == null) {
            throw new ParserException("The response need to be initialized");
        }
    }

    @Override
    public void setResponseContent(NetworkResponse networkResponse) throws ParserException {
        String response;
        try {
            if (networkResponse.headers != null
                    && HttpHeaderParser.parseCharset(networkResponse.headers) != null) {
                response = new String(networkResponse.data,
                        HttpHeaderParser.parseCharset(networkResponse.headers));
            } else {
                response = new String(networkResponse.data, "UTF-8");
            }
        } catch (UnsupportedEncodingException e1) {
            throw new ParserException(e1);
        }

        if (response != null && response.length() > 0) {
            if (response.charAt(0) == '[') {
                // VolleyLog.d("Subscription Response jsonParser.parseError(json); "
                // + response);
                try {
                    JSONArray json = new JSONArray(response);
                    setAltRestObject(json);
                } catch (JSONException e) {
                    throw new ParserException(e);
                }

            } else if (response.charAt(0) == '{') {
                // VolleyLog.d("Subscription Response jsonParser.parseError(json); "
                // + response);
                try {
                    JSONObject json = new JSONObject(response);
                    setRestObject(json);
                } catch (JSONException e) {
                    throw new ParserException(e);
                }

            } else {
                // VolleyLog.d("Subscription Response result.charAt(0) != '{' "
                // + response);
                throw new ParserException("UNSUPPORTED RESPONSE");
            }
        }
    }

}
