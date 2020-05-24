package com.yanzord.votingappservice.dto;

public class VoteDTO {
    private String associateId;
    private VoteChoice voteChoice;

    public VoteDTO(String associateId, VoteChoice voteChoice) {
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
