
package com.zagros.quiver.rest.core.builder.url.strategy;

/**
 * Build strategy, that generates the most used url query format
 * ?key1=value1&key2=value2&key3=value3....
 */
public final class AmpersandUrlBuildStrategy extends AbstractUrlBuildStrategy {

    public AmpersandUrlBuildStrategy() {
        mArgSeparator = "&";
        mArgAssigner = "=";
        mStartSymbol = "?";
    }

}
