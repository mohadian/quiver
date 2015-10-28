
package com.zagros.quiver.rest.core.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.zagros.quiver.rest.core.RestCall;
import com.zagros.quiver.rest.core.RestController;
import com.zagros.quiver.rest.core.RestRequest;
import com.zagros.quiver.rest.core.builder.content.RequestContentBuilder;
import com.zagros.quiver.rest.core.http.RestRequestManager;
import com.zagros.quiver.rest.core.http.SessionExpiredException;
import com.zagros.quiver.rest.core.listeners.RestResponseListener;
import com.zagros.quiver.rest.core.parser.RestResponseParser;

public class VolleyRestCallBuilder {

    public static <E extends Enum<E>> RestCall<E> build(E method,
            RequestContentBuilder contentBuilder,
            RestResponseParser parser, RestResponseListener<?> listener,
            Object owner, String... args) {
        Class<E> clazz = (Class<E>) method.getClass();
        RestRequest request = RestController.buildRequest(clazz, method, args);
        request.setContentBuilder(contentBuilder);
        RestCall<E> call = new RestCall<E>(owner, request, parser, listener)
                .setRemoteMethod(method);
        return call;
    }

    public static <E extends Enum<E>> String buildAndSend(Context context, E method,
            RequestContentBuilder contentBuilder, RestResponseParser parser,
            RestResponseListener<?> listener, Object owner, String... args) {
        RequestQueue queue = Volley.newRequestQueue(context);
        RestCall restcall = VolleyRestCallBuilder.build(method, contentBuilder, parser,
                listener, owner, args);
        VolleyRestCallRequest request = new VolleyRestCallRequest(restcall);
        queue.add(request);
        return "";
    }

    public static <E extends Enum<E>> String buildAndSend(Context context, E method,
            RequestContentBuilder contentBuilder, RestResponseParser parser,
            RestResponseListener<?> listener, Object owner, RestRequest.RequestPriority priority,
            String args) {
        RequestQueue queue = Volley.newRequestQueue(context);
        RestCall restcall = VolleyRestCallBuilder.build(method, contentBuilder, parser,
                listener, owner, args);
        restcall.getRequest().setPriority(priority);
        VolleyRestCallRequest request = new VolleyRestCallRequest(restcall);
        queue.add(request);
        return "";
    }

    public static <E extends Enum<E>> String resend(RestCall<E> restCall) {
        try {
            return RestRequestManager.getInstance().addRequest(restCall);
        } catch (SessionExpiredException e) {
            e.printStackTrace();
            return null;
        }
    }
}
