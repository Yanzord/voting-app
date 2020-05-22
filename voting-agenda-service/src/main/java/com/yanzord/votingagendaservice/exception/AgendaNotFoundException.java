package com.yanzord.votingagendaservice.exception;

public class AgendaNotFoundException extends RuntimeException {
    public AgendaNotFoundException(String message) {
        super(message);
    }
}
