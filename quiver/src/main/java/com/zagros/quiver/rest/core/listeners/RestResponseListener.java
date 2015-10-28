
package com.zagros.quiver.rest.core.listeners;

import com.android.volley.VolleyError;
import com.zagros.quiver.rest.core.RestCall;

/**
 * Listener that notifies when a request starts and completes
 *
 * @param <E>
 * @author Mostafa.Hadian
 */
public interface RestResponseListener<E extends Enum<E>> {

    /**
     * Called to notify that the request is completed successfully
     *
     * @param request
     */
    void onSuccess(RestCall<E> request);

    /**
     * Called to notify that the request is started
     *
     * @param request
     */
    void onProgressStarted(RestCall<E> request);

    /**
     * Called to notify that the request is finished
     *
     * @param request
     */
    void onProgressFinished(RestCall<E> request);

    /**
     * Called to notify that the request is completed unsuccessfully
     *
     * @param request
     * @param error
     */
    void onFailure(RestCall<E> request, VolleyError error);

}
