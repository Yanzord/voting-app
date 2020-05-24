package com.yanzord.votingappservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SessionDTO {
    private String id;
    private String agendaId;
    private long duration;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<VoteDTO> votes;
    private SessionStatus status;

    public SessionDTO() {}

    public SessionDTO(String agendaId, long duration, LocalDateTime startDate) {
        this.agendaId = agendaId;
        this.duration = duration;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
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

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }
}
