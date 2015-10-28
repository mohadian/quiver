
package com.zagros.quiver.rest.core.builder.url;

public class UrlBuilderException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UrlBuilderException(String message) {
        super(message);
    }

    public UrlBuilderException(Throwable cause) {
        super(cause);
    }

    public UrlBuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
