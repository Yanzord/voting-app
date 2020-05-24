package com.yanzord.votingagendaservice.model;

public class Vote {
    private String associateId;
    private String associateCPF;
    private VoteChoice voteChoice;

    public Vote(String associateId, String associateCPF, VoteChoice voteChoice) {
        this.associateId = associateId;
        this.associateCPF = associateCPF;
        this.voteChoice = voteChoice;
    }

    public String getAssociateId() {
        return associateId;
    }

    public void setAssociateId(String associateId) {
        this.associateId = associateId;
    }

    public String getAssociateCPF() {
        return associateCPF;
    }

    public void setAssociateCPF(String associateCPF) {
        this.associateCPF = associateCPF;
    }

    public VoteChoice getVoteChoice() {
        return voteChoice;
    }

    public void setVoteChoice(VoteChoice voteChoice) {
        this.voteChoice = voteChoice;
    }
}
