
package com.zagros.quiver.rest.core.parser;

import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;

/**
 * Default implemetation for Json parser
 */
public class AbstractXmlResponseParser extends RestResponseParser<String, String> {

    public AbstractXmlResponseParser() {

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
            setRestObject(response);
        }
    }

}
