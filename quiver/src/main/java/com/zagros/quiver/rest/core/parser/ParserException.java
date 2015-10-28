
package com.zagros.quiver.rest.core.parser;

public class ParserException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 50051586822161444L;

    public ParserException(String message) {
        super(message);
    }

    public ParserException(Throwable cause) {
        super(cause);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
