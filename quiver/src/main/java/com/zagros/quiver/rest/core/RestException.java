
package com.zagros.quiver.rest.core;

/**
 * Encapsulation of a Rest request exception
 *
 * @author Mostafa.Hadian
 */
public class RestException extends Exception {
    private static final long serialVersionUID = 1L;

    public RestException() {

    }

    public RestException(String message) {
        super(message);
    }

    public RestException(Throwable cause) {
        super(cause);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }
}
