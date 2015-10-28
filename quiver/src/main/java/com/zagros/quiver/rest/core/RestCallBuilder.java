
package com.zagros.quiver.rest.core;

import com.zagros.quiver.rest.core.builder.content.RequestContentBuilder;
import com.zagros.quiver.rest.core.http.RestRequestManager;
import com.zagros.quiver.rest.core.http.SessionExpiredException;
import com.zagros.quiver.rest.core.listeners.RestResponseListener;
import com.zagros.quiver.rest.core.parser.AbstractJsonResponseParser;

public class RestCallBuilder {

    private static <E extends Enum<E>> RestCall<E> build(E method,
                                                         RequestContentBuilder contentBuilder,
                                                         AbstractJsonResponseParser parser, RestResponseListener<?> listener,
                                                         Object owner, String... args) {
        Class<E> clazz = (Class<E>) method.getClass();
        RestRequest request = RestController.buildRequest(clazz, method, args);
        request.setContentBuilder(contentBuilder);
        RestCall<E> call = new RestCall<E>(owner, request, parser, listener)
                .setRemoteMethod(method);
        return call;
    }

    public static <E extends Enum<E>> String buildAndSend(E method,
                                                          RequestContentBuilder contentBuilder, AbstractJsonResponseParser parser,
                                                          RestResponseListener<?> listener, Object owner, String... args) {
        try {
            return RestRequestManager.getInstance().addRequest(
                    RestCallBuilder.build(method, contentBuilder, parser, listener, owner, args));
        } catch (SessionExpiredException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <E extends Enum<E>> String buildAndSend(E method,
                                                          RequestContentBuilder contentBuilder, AbstractJsonResponseParser parser,
                                                          RestResponseListener<?> listener, Object owner, RestRequest.RequestPriority priority,
                                                          String... args) {
        try {
            RestCall<?> call = RestCallBuilder.build(method, contentBuilder, parser, listener,
                    owner, args);
            call.getRequest().setPriority(priority);
            return RestRequestManager.getInstance().addRequest(call);
        } catch (SessionExpiredException e) {
            e.printStackTrace();
            return null;
        }
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
