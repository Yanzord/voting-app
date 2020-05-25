package com.yanzord.votingappservice.exception;

public class NewAgendaException extends Exception {
    public NewAgendaException() {
        super("Agenda is new, there is no result.");
    }
}
