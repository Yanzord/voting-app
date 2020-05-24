package com.yanzord.votingsessionservice.exception;

public class ClosedSessionException extends Exception {
    public ClosedSessionException(String message) {
        super(message);
    }
}
