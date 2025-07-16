package com.github.juliherms.clienteService.exception;

public class DuplicateCpfException extends RuntimeException {

    public DuplicateCpfException(String message) {
        super(message);
    }

    public DuplicateCpfException(String message, Throwable cause) {
        super(message, cause);
    }
}

