package com.yanzord.votingsessionservice.exception;

public class SessionNotFoundException extends Exception {
    public SessionNotFoundException() {
        super("Session not found.");
    }
}
