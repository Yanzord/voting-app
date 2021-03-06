package com.yanzord.votingappservice.model;

public class Agenda {
    private String id;
    private String description;
    private AgendaResult agendaResult;
    private AgendaStatus status;

    public Agenda() {}

    public Agenda(String id, String description, AgendaStatus status) {
        this.id = id;
        this.description = description;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AgendaResult getAgendaResult() {
        return agendaResult;
    }

    public void setAgendaResult(AgendaResult agendaResult) {
        this.agendaResult = agendaResult;
    }

    public AgendaStatus getStatus() {
        return status;
    }

    public void setStatus(AgendaStatus status) {
        this.status = status;
    }
}
