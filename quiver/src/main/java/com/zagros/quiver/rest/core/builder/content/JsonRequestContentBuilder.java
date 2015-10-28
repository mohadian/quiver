
package com.zagros.quiver.rest.core.builder.content;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public abstract class JsonRequestContentBuilder implements RequestContentBuilder {
    private static final String TAG = JsonRequestContentBuilder.class.getSimpleName();

    protected abstract JSONObject buildJson(JSONObject jsonObject) throws JSONException;

    @Override
    public HttpEntity getEntity() {
        HttpEntity httpEntity = null;

        try {
            JSONObject jsonObject = buildJson(new JSONObject());
            httpEntity = new StringEntity(jsonObject.toString(), "UTF-8");
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "UnsupportedEncodingException", e);
        } catch (NullPointerException e) {
            Log.e(TAG, "NullPointerException", e);
        }

        return httpEntity != null ? httpEntity : new BasicHttpEntity();
    }

    @Override
    public byte[] getContentBytes(String protocolCharset) throws UnsupportedEncodingException {
        JSONObject jsonObject;
        try {
            jsonObject = buildJson(new JSONObject());
            String json = jsonObject.toString();
            return json == null ? null : json.getBytes(protocolCharset);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected JSONArray buildStringArray(String[] stringArray) {
        JSONArray jsonStringArray = new JSONArray();
        for (String string : stringArray) {
            jsonStringArray.put(string);
        }
        return jsonStringArray;
    }
}
