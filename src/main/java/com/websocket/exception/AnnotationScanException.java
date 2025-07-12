package com.websocket.exception;

public class AnnotationScanException extends RuntimeException {
    public AnnotationScanException(String message) {
        super(message);
    }
    public AnnotationScanException(String message, Throwable cause) {
        super(message, cause);
    }
}
