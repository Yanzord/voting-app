package com.yanzord.votingappservice.exception;

public class ClosedSessionException extends Exception {
    public ClosedSessionException() {
        super("Session is closed.");
    }
}
