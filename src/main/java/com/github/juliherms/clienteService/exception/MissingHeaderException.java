package com.github.juliherms.clienteService.exception;

public class MissingHeaderException extends RuntimeException {

    public MissingHeaderException(String message) {
        super(message);
    }

    public MissingHeaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
