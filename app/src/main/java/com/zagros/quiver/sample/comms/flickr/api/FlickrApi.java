package com.zagros.quiver.sample.comms.flickr.api;

import com.zagros.quiver.rest.core.RestController;
import com.zagros.quiver.rest.core.RestRequest;
import com.zagros.quiver.rest.core.ServiceApi;
import com.zagros.quiver.rest.core.builder.url.RequestUrlSpec;
import com.zagros.quiver.rest.core.builder.url.UrlKey;
import com.zagros.quiver.rest.core.builder.url.strategy.AmpersandUrlBuildStrategy;

public class FlickrApi extends ServiceApi<FlickrApi.Method> {

    public enum Method{
        SEARCH
    }

    public FlickrApi() {
        super(FlickrConstants.URL_FLICKR_BASE_URL, new AmpersandUrlBuildStrategy());
    }

    @Override
    protected void initMethods() {
        addApiCall(new Call<FlickrApi.Method>()
                .setKey(Method.SEARCH)
                .setMethod(RestRequest.Method.GET)
                .setUrlSpec(
                        new RequestUrlSpec(FlickrConstants.URL_FLICKR_PHOTOS_PUBLIC_URL)
                                .addUrlKey(
                                        new UrlKey(
                                                FlickrConstants.PARAM_ID,
                                                "", "", true, true))
                                .addUrlKey(
                                        new UrlKey(
                                                FlickrConstants.PARAM_IDS,
                                                "", "", true, true))
                                .addUrlKey(
                                        new UrlKey(
                                                FlickrConstants.PARAM_TAGS,
                                                "", "", true, true))
                                .addUrlKey(
                                        new UrlKey(
                                                FlickrConstants.PARAM_TAG_MODE,
                                                "", "", true, true))
                                .addUrlKey(
                                        new UrlKey(
                                                FlickrConstants.PARAM_FORMAT,
                                                "", "", true, true))
                                .addUrlKey(
                                        new UrlKey(
                                                FlickrConstants.PARAM_NO_JSON_CALLBACK,
                                                "", "", true, true))
                                .addUrlKey(
                                        new UrlKey(
                                                FlickrConstants.PARAM_LANGUAGE ,
                                                "", "", true, true))
                )
                .setIsAnonymous(true));
    }

    @Override
    protected void addToRepository() {
        RestController.getInstance().addRepository(this, FlickrApi.Method.class);
    }

    @Override
    protected void defaultHeaders() {

    }
}
