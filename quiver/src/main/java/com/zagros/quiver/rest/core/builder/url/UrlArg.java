
package com.zagros.quiver.rest.core.builder.url;

/**
 * Wrapper around URL value. Helps to avoid unpredictable NullPointerExceptions
 * when working with collections and other objects.
 */
public class UrlArg {
    private String mValue;

    public UrlArg(String value) {
        mValue = value;
    }

    public String getValue() {
        return mValue;
    }

    public boolean isNull() {
        return mValue == null;
    }
}
