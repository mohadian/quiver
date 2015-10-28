
package com.zagros.quiver.rest.core.builder.url.strategy;

import com.zagros.quiver.rest.core.builder.url.UrlKey;

/**
 * Default url building strategy implementation.
 */
public abstract class AbstractUrlBuildStrategy implements UrlBuildStrategy {
    protected String mArgSeparator;
    protected String mArgAssigner;
    protected String mStartSymbol;

    @Override
    public String getStartSymbol() {
        return mStartSymbol;
    }

    /**
     * Builds first entry. Start symbol gets prepended in front of the entry.
     * The caller is responsible that this method is called only for the first
     * valid argument.
     */
    @Override
    public String buildFirst(UrlKey urlKey, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(mStartSymbol);
        buildArg(sb, urlKey, value);
        return sb.toString();
    }

    /**
     * Builds url query entry.
     */
    @Override
    public String buildRest(UrlKey urlKey, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(mArgSeparator);
        buildArg(sb, urlKey, value);
        return sb.toString();
    }

    /**
     * Builds single url entry(argument and key) and appends it to current
     * string builder. If isUsedInUrl() fails, then only the value is appended.
     * Prefix and suffix gets taken into account also
     *
     * @param sb
     * @param urlKey
     * @param value
     * @return
     */
    protected StringBuilder buildArg(StringBuilder sb, UrlKey urlKey, String value) {
        sb.append(urlKey.getPrefix());
        if (urlKey.isUsedInUrl()) {
            sb.append(urlKey.getValue());
            sb.append(mArgAssigner);
        }
        sb.append(value);
        sb.append(urlKey.getSuffix());
        return sb;
    }
}
