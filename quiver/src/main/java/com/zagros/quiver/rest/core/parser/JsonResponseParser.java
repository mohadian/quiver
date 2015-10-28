
package com.zagros.quiver.rest.core.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface JsonResponseParser {
    void parse(JSONObject json) throws JSONException, ParserException;

    void parseError(JSONObject json) throws JSONException, ParserException;

    void parse(JSONArray json) throws JSONException, ParserException;
}
