
package com.zagros.quiver.rest.core;

/**
 * Encapsulation of a RequestBuilder Exception
 *
 * @author Mostafa.Hadian
 */
public class RequestBuilderException extends Exception {

    public RequestBuilderException(String message) {
        super(message);
    }

    public RequestBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    private static final long serialVersionUID = 1L;

}
