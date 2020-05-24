package com.yanzord.votingappservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SessionDTO {
    private String id;
    private String agendaId;
    private long timeout;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<VoteDTO> votes;

    public SessionDTO() {}

    public SessionDTO(String agendaId, long timeout, LocalDateTime startDate) {
        this.agendaId = agendaId;
        this.timeout = timeout;
        this.startDate = startDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgendaId() {
        return agendaId;
    }

    public void setAgendaId(String agendaId) {
        this.agendaId = agendaId;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
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

    public List<VoteDTO> getVotes() {
        return votes;
    }

    public void setVotes(List<VoteDTO> votes) {
        this.votes = votes;
    }
}
