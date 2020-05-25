package com.yanzord.votingappservice.exception;

public class FinishedAgendaException extends Exception {
    public FinishedAgendaException() {
        super("Agenda is finished.");
    }
}
