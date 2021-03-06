package com.yanzord.votingappservice.model;

public class AgendaResult {
    private long totalUpVotes;
    private long totalDownVotes;
    private String result;

    public AgendaResult() {}

    public AgendaResult(long totalUpVotes, long totalDownVotes, String result) {
        this.totalUpVotes = totalUpVotes;
        this.totalDownVotes = totalDownVotes;
        this.result = result;
    }

    public long getTotalUpVotes() {
        return totalUpVotes;
    }

    public void setTotalUpVotes(long totalUpVotes) {
        this.totalUpVotes = totalUpVotes;
    }

    public long getTotalDownVotes() {
        return totalDownVotes;
    }

    public void setTotalDownVotes(long totalDownVotes) {
        this.totalDownVotes = totalDownVotes;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
