package com.yanzord.votingagendaservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document
public class VotingAgenda {
    @Id
    private String id;
    private String description;
    private List<Vote> votes;
    private LocalDateTime votingStart;
    private LocalDateTime votingEnd;
    private boolean progressStatus;

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

    public LocalDateTime getVotingStart() {
        return votingStart;
    }

    public void setVotingStart(LocalDateTime votingStart) {
        this.votingStart = votingStart;
    }

    public LocalDateTime getVotingEnd() {
        return votingEnd;
    }

    public void setVotingEnd(LocalDateTime votingEnd) {
        this.votingEnd = votingEnd;
    }

    public boolean isProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(boolean progressStatus) {
        this.progressStatus = progressStatus;
    }
}
