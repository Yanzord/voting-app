package com.yanzord.votingagendaservice.dto;

import com.yanzord.votingagendaservice.model.AgendaStatus;
import com.yanzord.votingagendaservice.model.Vote;

import java.time.LocalDateTime;
import java.util.List;

public class ClosedAgendaDTO {
    private String id;
    private List<Vote> votes;
    private LocalDateTime endDate;
    private AgendaStatus status;

    public ClosedAgendaDTO(String id, List<Vote> votes, LocalDateTime endDate, AgendaStatus status) {
        this.id = id;
        this.votes = votes;
        this.endDate = endDate;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
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
