package com.yanzord.votingagendaservice.model;

import org.springframework.stereotype.Component;

@Component
public class Vote {
    private String associateId;
    private VoteChoice voteChoice;

    public String getAssociateId() {
        return associateId;
    }

    public void setAssociateId(String associateId) {
        this.associateId = associateId;
    }

    public VoteChoice getVoteChoice() {
        return voteChoice;
    }

    public void setVoteChoice(VoteChoice voteChoice) {
        this.voteChoice = voteChoice;
    }
}
