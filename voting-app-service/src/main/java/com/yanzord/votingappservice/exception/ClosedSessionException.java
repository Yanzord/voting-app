package com.yanzord.votingappservice.exception;

public class ClosedSessionException extends Exception {
    public ClosedSessionException(String message) {
        super(message);
    }
}
