
package com.zagros.quiver.rest.core.builder.url.strategy;

/**
 * Build strategy, that generate query where keys and values are separated with
 * slashes /key1/value1/key2/value2/key3/value3...
 */
public final class SlashUrlBuildStrategy extends AbstractUrlBuildStrategy {

    public SlashUrlBuildStrategy() {
        mArgSeparator = "/";
        mArgAssigner = "/";
        mStartSymbol = "/";
    }

}
