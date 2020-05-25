package com.yanzord.votingagendaservice.exception;

public class AgendaNotFoundException extends Exception {
    public AgendaNotFoundException() {
        super("Voting agenda not found.");
    }
}
