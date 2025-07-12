package com.websocket.exception;

public class DocGenException extends RuntimeException {
    public DocGenException(String message) {
        super(message);
    }
    public DocGenException(String message, Throwable cause) {
        super(message, cause);
    }
}
