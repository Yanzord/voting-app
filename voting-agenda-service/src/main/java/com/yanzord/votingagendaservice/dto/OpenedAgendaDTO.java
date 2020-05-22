package com.yanzord.votingagendaservice.dto;

import com.yanzord.votingagendaservice.model.AgendaStatus;

import java.time.LocalDateTime;

public class OpenedAgendaDTO {
    private String id;
    private LocalDateTime startDate;
    private AgendaStatus status;

    public OpenedAgendaDTO(String id, LocalDateTime startDate, AgendaStatus status) {
        this.id = id;
        this.startDate = startDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public AgendaStatus getStatus() {
        return status;
    }

    public void setStatus(AgendaStatus status) {
        this.status = status;
    }
}
