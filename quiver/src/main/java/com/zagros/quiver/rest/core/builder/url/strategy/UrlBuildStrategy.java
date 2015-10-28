
package com.zagros.quiver.rest.core.builder.url.strategy;

import com.zagros.quiver.rest.core.builder.url.UrlKey;

/**
 * Url generation strategy interface. Used when generating key value pairs, that
 * will follow after action. For example if we have URL
 * http://localhost/users.php?name=foo&surname=bar Then build strategy is
 * responsible for building everything that follows question mark, including
 * question mark itself.
 */
public interface UrlBuildStrategy {
    String buildFirst(UrlKey urlKey, String value);

    String buildRest(UrlKey urlKey, String value);

    String getStartSymbol();
}
