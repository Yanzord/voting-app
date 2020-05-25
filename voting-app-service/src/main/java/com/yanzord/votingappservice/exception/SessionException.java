package com.yanzord.votingappservice.exception;

public class SessionException extends Throwable {
    public SessionException() {
        super("Session already created.");
    }
}
