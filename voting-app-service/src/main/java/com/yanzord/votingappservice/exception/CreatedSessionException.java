package com.yanzord.votingappservice.exception;

public class CreatedSessionException extends Throwable {
    public CreatedSessionException() {
        super("Session already created.");
    }
}
