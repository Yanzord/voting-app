package com.yanzord.votingappservice.dto;

public class VoteDTO {
    private String associateId;
    private String associateCPF;
    private VoteChoice voteChoice;

    public VoteDTO() {}

    public VoteDTO(String associateId, String associateCPF, VoteChoice voteChoice) {
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
