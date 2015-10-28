
package com.zagros.quiver.rest.core.http;

/**
 * Exception thrown when the current session is expired and the library is
 * trying to send a non anonymous request to the server.
 *
 * @author Mostafa.Hadian
 */
public class SessionExpiredException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 3594324779458686L;

}
