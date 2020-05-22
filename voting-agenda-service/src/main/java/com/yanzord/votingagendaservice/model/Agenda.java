package com.yanzord.votingagendaservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document
public class Agenda {
    @Id
    private String id;
    private String description;
    private List<Vote> votes;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private AgendaStatus status;

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

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public AgendaStatus getStatus() {
        return status;
    }

    public void setStatus(AgendaStatus status) {
        this.status = status;
    }
}
