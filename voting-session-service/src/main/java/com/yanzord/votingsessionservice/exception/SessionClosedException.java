package com.yanzord.votingsessionservice.exception;

public class SessionClosedException extends Exception {
    public SessionClosedException(String message) {
        super(message);
    }
}
