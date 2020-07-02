package com.emintolgahanpolat.api.exceptions;

public class ProcessException extends RuntimeException {


    /**
     * Constructs an {@code AuthenticationException} with the specified message and root
     * cause.
     *
     * @param msg the detail message
     * @param t the root cause
     */
    public ProcessException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * Constructs an {@code AuthenticationException} with the specified message and no
     * root cause.
     *
     * @param msg the detail message
     */
    public ProcessException(String msg) {
        super(msg);
    }

}
