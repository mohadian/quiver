
package com.zagros.quiver.rest.core.builder.url;

import android.util.Log;

import com.zagros.quiver.rest.core.builder.url.strategy.SlashUrlBuildStrategy;
import com.zagros.quiver.rest.core.builder.url.strategy.UrlBuildStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Builds urls. Uses RequestUrlSpec and actual argument values to transform them
 * into valid URL string, that can be used for requesting the network. Singleton
 * class.
 */
public class UrlBuilder {
    private static UrlBuilder sInstance = null;

    protected static String sDefaultRootUrl;
    protected static UrlBuildStrategy sDefaultUrlBuildStrategy;
    protected static UrlBuildStrategy sDefaultActionUrlBuildStrategy;

    static {
        sDefaultRootUrl = "";
        sDefaultUrlBuildStrategy = null;
        sDefaultActionUrlBuildStrategy = new SlashUrlBuildStrategy();
    }

    /**
     * Proxy for StringBuilder. Helps to detect how many arguments are currently
     * processed.
     */
    private static class UrlStringBuilder {
        private final StringBuilder mStringBuilder;
        private int mArgsAppended = 0;

        public UrlStringBuilder() {
            mStringBuilder = new StringBuilder();
        }

        public UrlStringBuilder append(String string) {
            mStringBuilder.append(string);

            return this;
        }

        public UrlStringBuilder appendArg(String string) {
            mStringBuilder.append(string);
            mArgsAppended++;
            return this;
        }

        public boolean haveNoArgsAppended() {
            return mArgsAppended == 0;
        }

        @Override
        public String toString() {
            return mStringBuilder.toString();
        }

        public void normalizeUrl(String arg) {
            if (arg != null) {
                int argsLen = arg.length();
                int argsLocation = mStringBuilder.lastIndexOf(arg);
                if (argsLocation > 0 && argsLocation == mStringBuilder.length() - argsLen) {
                    mStringBuilder.delete(argsLocation, mStringBuilder.length());
                }
            }
        }
    }

    private UrlBuilder() {
    }

    public static synchronized UrlBuilder getInstance() {
        if (sInstance == null) {
            sInstance = new UrlBuilder();
        }
        return sInstance;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Cloning not supported for singleton");
    }

    /**
     * Builds url from url spec and returns it's string representation. If no
     * string arguments are passed then builds only root url with appended
     * action. If there are arguments then tries to build and append them too.
     *
     * @param urlSpec
     * @param args
     * @return
     * @throws UrlBuilderException
     */
    public String buildUrl(RequestUrlSpec urlSpec, String... args) throws UrlBuilderException {
        UrlStringBuilder sb = new UrlStringBuilder();
        getInitializedBaseUrl(sb, urlSpec, args);

        if (args != null && args.length > 0) {
            buildUrlArgs(sb, urlSpec, false, args);
        }

        return sb.toString();
    }

    private UrlStringBuilder buildUrlArgs(UrlStringBuilder stringBuilder, RequestUrlSpec urlSpec,
                                          boolean isActionUrl, String[] args) throws UrlBuilderException {
        UrlBuildStrategy urlBuildStrategy = resolveStrategy(urlSpec, isActionUrl);
        List<UrlKey> urlKeys = new ArrayList<UrlKey>(urlSpec.getUrlKeyList());
        Queue<UrlArg> argQueue = getArgQueueFrom(args);

        for (UrlKey urlKey : urlKeys) {
            if (!argQueue.isEmpty()) {
                UrlArg arg = argQueue.remove();

                if (arg.isNull()) {
                    if (urlKey.isOptional()) {
                        continue;
                    } else {
                        throw new RuntimeException("UrlBuilder mandataory argument for key <"
                                + urlKey.getValue() + "> missing, aborting");
                    }

                } else {
                    if (isActionUrl) {
                        if (urlKey.isUsedActionUrl()) {
                            stringBuilder.normalizeUrl(urlBuildStrategy.getStartSymbol());
                            stringBuilder.append(urlBuildStrategy.buildRest(urlKey,
                                    arg.getValue()));
                        }
                    } else {
                        if (!urlKey.isUsedActionUrl()) {
                            if (stringBuilder.haveNoArgsAppended()) {
                                stringBuilder.normalizeUrl(urlBuildStrategy.getStartSymbol());
                                if (stringBuilder.haveNoArgsAppended())
                                    stringBuilder
                                            .appendArg(urlBuildStrategy.buildFirst(urlKey,
                                                    arg.getValue()));
                            } else {
                                stringBuilder.appendArg(urlBuildStrategy.buildRest(urlKey,
                                        arg.getValue()));
                            }
                        }
                    }
                }
            } else {
                handleEmptyArgForKey(urlKey);
            }
        }

        return stringBuilder;
    }

    private UrlStringBuilder getInitializedBaseUrl(UrlStringBuilder sb, RequestUrlSpec urlSpec,
                                                   String[] args)
            throws UrlBuilderException {
        String rootUrl = resolveRootUrl(urlSpec);
        String actionUrl = resolveActionUrl(urlSpec, args);

        sb.append(rootUrl);

        if (actionUrl != null && actionUrl.length() > 0) {
            sb.append("/").append(actionUrl);
        }
        if (args != null && args.length > 0) {
            buildUrlArgs(sb, urlSpec, true, args);
        }

        return sb;
    }

    private String resolveRootUrl(RequestUrlSpec urlSpec) throws UrlBuilderException {
        String rootUrl = sDefaultRootUrl;

        if (urlSpec.isRootUrlSet()) {
            rootUrl = urlSpec.getRootUrl();
        }

        if (rootUrl.length() == 0) {
            throw new UrlBuilderException("root url not set");
        }

        return rootUrl;
    }

    private String resolveActionUrl(RequestUrlSpec urlSpec, String[] args)
            throws UrlBuilderException {
        String actionUrl = urlSpec.getActionUrl();
        if (args != null) {

        }
        if (actionUrl == null) {
            throw new UrlBuilderException("action url not set, aborting");
        }
        return actionUrl;
    }

    private UrlBuildStrategy resolveStrategy(RequestUrlSpec urlSpec, boolean isActionUrl)
            throws UrlBuilderException {
        UrlBuildStrategy urlBuildStrategy = sDefaultUrlBuildStrategy;
        if (isActionUrl) {
            urlBuildStrategy = sDefaultActionUrlBuildStrategy;
        } else {
            if (urlSpec.isBuildStrategySet()) {
                urlBuildStrategy = urlSpec.getBuildStrategy();
            }

            if (urlBuildStrategy == null) {
                throw new UrlBuilderException("url build strategy not set");
            }
        }

        return urlBuildStrategy;
    }

    /**
     * Creates Queue from String array, that will be used for building query.
     *
     * @param args
     * @return
     * @throws UrlBuilderException
     */
    private Queue<UrlArg> getArgQueueFrom(String args[]) throws UrlBuilderException {
        if (args.length == 0) {
            throw new UrlBuilderException("no arguments available to build queue");
        }
        Queue<UrlArg> argQueue = new ArrayBlockingQueue<UrlArg>(args.length);
        for (String arg : args) {
            argQueue.add(new UrlArg(arg));
        }
        return argQueue;
    }

    private void handleEmptyArgForKey(UrlKey urlKey) throws UrlBuilderException {
        if (!urlKey.isOptional()) {
            throw new UrlBuilderException(
                    "argument queue is empty, but request spec contains unassigned mandatory field");
        }
    }

    /**
     * Creates specimen url for spec. Usable for getting information about
     * current request. Which arguments are mandatory, which optional, and how
     * the actual request should look, when getting sent to the server.
     *
     * @param urlSpec
     * @return
     * @throws UrlBuilderException
     */
    public String buildSpecimenUrl(RequestUrlSpec urlSpec,
                                   boolean isActionUrl, String[] args) throws UrlBuilderException {
        UrlStringBuilder sb = new UrlStringBuilder();
        getInitializedBaseUrl(sb, urlSpec, null);

        UrlBuildStrategy urlBuildStrategy = resolveStrategy(urlSpec, isActionUrl);
        List<UrlKey> urlKeys = new ArrayList<UrlKey>(urlSpec.getUrlKeyList());

        for (UrlKey urlKey : urlKeys) {
            String builtArg = sb.haveNoArgsAppended() ?
                    urlBuildStrategy.buildFirst(urlKey, getSpecimenValueFrom(urlKey)) :
                    urlBuildStrategy.buildRest(urlKey, getSpecimenValueFrom(urlKey));
            if (urlKey.isOptional()) {
                sb.append("[").appendArg(builtArg).append("]");
            } else {
                sb.appendArg(builtArg);
            }
        }

        return sb.toString();
    }

    public String buildSpecimenUrlSafe(RequestUrlSpec urlSpec, String[] args) {
        String url = "INVALID";
        try {
            url = buildSpecimenUrl(urlSpec, false, args);
        } catch (UrlBuilderException e) {
            Log.e(UrlBuilder.class.getSimpleName(), "specimen url building failed", e);
        }
        return url;
    }

    private String getSpecimenValueFrom(UrlKey urlKey) {
        UrlStringBuilder sb = new UrlStringBuilder();
        sb.append("<").append(urlKey.getValue()).append("_value>");
        return sb.toString();
    }

    public static void setDefaultRootUrl(String rootUrl) {
        sDefaultRootUrl = rootUrl;
    }

    public static void setDefaultBuildStrategy(UrlBuildStrategy buildStrategy) {
        sDefaultUrlBuildStrategy = buildStrategy;
    }

    public static String getDefaultRootUri() {
        return sDefaultRootUrl;
    }
}
