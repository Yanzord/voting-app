package com.yanzord.votingagendaservice.model;

public class Vote {
    private String associateId;
    private VoteChoice voteChoice;

    public Vote(String associateId, VoteChoice voteChoice) {
        this.associateId = associateId;
        this.voteChoice = voteChoice;
    }

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
