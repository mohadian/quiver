package com.zagros.quiver.rest.core.builder.url;

import com.zagros.quiver.rest.core.builder.url.strategy.UrlBuildStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that specifies single URL for web service call request. Contains all
 * possible arguments that can be used with this request.
 */
public class RequestUrlSpec {
    public static final int MIN_URL_KEY_RESERVE = 5;

    /*
     * Root url e.g. http://localhost
     */
    protected String mRootUrl;

    /*
     * everything that follows after root url (excluding slash) for example, if
     * the call action url is http://localhost/brands, then mActionUrl will
     * contain value 'brands'
     */
    protected String mActionUrl;

    /*
     * Strategy which is used for building this url string, is left to null, the
     * default one from UrlBuilder will be used if any.
     */
    protected UrlBuildStrategy mBuildStrategy = null;

    /*
     * List with all possible request keys.
     */
    protected List<UrlKey> mUrlKeyList;

    public RequestUrlSpec() {
        mRootUrl = "";
        mActionUrl = "";
        mUrlKeyList = new ArrayList<>(MIN_URL_KEY_RESERVE);
    }

    public RequestUrlSpec(String actionUrl) {
        this();
        mActionUrl = actionUrl;
    }

    public RequestUrlSpec addUrlKey(UrlKey urlKey) {
        mUrlKeyList.add(urlKey);
        return this;
    }

    public List<UrlKey> getUrlKeyList() {
        return mUrlKeyList;
    }

    public boolean isRootUrlSet() {
        return mRootUrl.length() != 0;
    }

    public RequestUrlSpec setRootUrl(String rootUrl) {
        mRootUrl = rootUrl;
        return this;
    }

    public String getRootUrl() {
        return mRootUrl;
    }

    public String getActionUrl() {
        return mActionUrl;
    }

    public boolean isBuildStrategySet() {
        return mBuildStrategy != null;
    }

    public RequestUrlSpec setBuildStrategy(UrlBuildStrategy urlBuildStrategy) {
        mBuildStrategy = urlBuildStrategy;
        return this;
    }

    public UrlBuildStrategy getBuildStrategy() {
        return mBuildStrategy;
    }
}
