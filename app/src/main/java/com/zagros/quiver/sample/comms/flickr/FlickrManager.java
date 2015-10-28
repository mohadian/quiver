package com.zagros.quiver.sample.comms.flickr;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.zagros.quiver.rest.core.RestCall;
import com.zagros.quiver.rest.core.RestRequest;
import com.zagros.quiver.rest.core.builder.content.EmptyContentBuilder;
import com.zagros.quiver.rest.core.builder.content.RequestContentBuilder;
import com.zagros.quiver.rest.core.listeners.RestResponseListener;
import com.zagros.quiver.rest.core.parser.AbstractJsonResponseParser;
import com.zagros.quiver.rest.core.volley.RestVolley;
import com.zagros.quiver.rest.core.volley.VolleyRestCallBuilder;
import com.zagros.quiver.rest.core.volley.VolleyRestCallRequest;
import com.zagros.quiver.sample.comms.flickr.api.FlickrApi;
import com.zagros.quiver.sample.comms.flickr.api.FlickrConstants;
import com.zagros.quiver.sample.comms.flickr.parsers.FlickrPhotosParser;

/**
 * Created by Mostafa on 26/10/2015.
 */
public class FlickrManager {

    private static final String COMMS_TAG = "FlickrApi";
    FlickrApi flickrApi;

    private RequestQueue mRequestQueue;
    private RetryPolicy mRetryPolicy;

    /** The default socket timeout in milliseconds */
    public static final int DEFAULT_TIMEOUT_MS = 5000;
    /** The default number of retries */
    public static final int DEFAULT_MAX_RETRIES = 1;
    /** The default backoff multiplier */
    public static final float DEFAULT_BACKOFF_MULT = 1f;

    private FlickrManager(){
        flickrApi = new FlickrApi();
    }

    public void init(Context context){
        mRetryPolicy = new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES,
                DEFAULT_BACKOFF_MULT);
        mRequestQueue = RestVolley.newRequestQueue(context);
    }

    private static class FlickrManagerHolder {
        public static final FlickrManager INSTANCE = new FlickrManager();
    }

    public static FlickrManager instance(){
        return FlickrManagerHolder.INSTANCE;
    }

    public void getPublicPhotos(RestResponseListener<FlickrApi.Method> listener, String id,
                                  String ids, String tag, String tagMode, String language){
        buildAndSend(FlickrApi.Method.SEARCH, new EmptyContentBuilder(), new FlickrPhotosParser(), listener, this, id, ids, tag, tagMode, FlickrConstants.FORMAT_JSON, FlickrConstants.NO_JSON_CALLBACK, language);
    }


    /**
     * Rest manager
     */

    public <E extends Enum<E>> void buildAndSend(E method,
                                                 RequestContentBuilder contentBuilder, AbstractJsonResponseParser parser,
                                                 RestResponseListener<?> listener, Object owner) {

        RestCall<E> restcall = VolleyRestCallBuilder.build(method, contentBuilder, parser,
                listener, owner);
        VolleyRestCallRequest request = new VolleyRestCallRequest(restcall);
        if (owner != null) {
            request.setTag(owner);
        } else {
            request.setTag(COMMS_TAG);
        }
        request.setRetryPolicy(mRetryPolicy);
        mRequestQueue.add(request);
    }
    public <E extends Enum<E>> void buildAndSend(E method,
                                                 RequestContentBuilder contentBuilder, AbstractJsonResponseParser parser,
                                                 RestResponseListener<?> listener, Object owner, String... args) {

        RestCall<E> restcall = VolleyRestCallBuilder.build(method, contentBuilder, parser,
                listener, owner, args);
        VolleyRestCallRequest request = new VolleyRestCallRequest(restcall);
        if (owner != null) {
            request.setTag(owner);
        } else {
            request.setTag(COMMS_TAG);
        }
        request.setRetryPolicy(mRetryPolicy);
        mRequestQueue.add(request);
    }
    public <E extends Enum<E>> void buildAndSend(E method,
                                                 RequestContentBuilder contentBuilder, AbstractJsonResponseParser parser,
                                                 RestResponseListener<?> listener, Object owner, RestRequest.RequestPriority priority) {

        RestCall<E> restcall = VolleyRestCallBuilder.build(method, contentBuilder, parser,
                listener, owner);
        restcall.getRequest().setPriority(priority);
        VolleyRestCallRequest request = new VolleyRestCallRequest(restcall);
        if (owner != null) {
            request.setTag(owner);
        } else {
            request.setTag(COMMS_TAG);
        }
        request.setRetryPolicy(mRetryPolicy);
        mRequestQueue.add(request);
    }

    public void cancelAll(Object owner) {
        mRequestQueue.cancelAll(owner);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cannot clone this class");
    }
}
