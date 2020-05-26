package com.yanzord.votingagendaservice.exception;

public class AgendaNotFoundException extends Exception {
    public AgendaNotFoundException() {
        super("Agenda not found.");
    }
}
