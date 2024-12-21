package com.kewebsi.errorhandling;

public class BackendDataException extends RuntimeException {

    private static final long serialVersionUID = -3572591263295844268L;

    public BackendDataException() {
        super();
    }

    public BackendDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BackendDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public BackendDataException(String message) {
        super(message);
    }

    public BackendDataException(Throwable cause) {
        super(cause);
    }

}