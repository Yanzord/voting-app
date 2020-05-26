package com.yanzord.votingappservice.exception;

public class VoteException extends Exception {
    public VoteException() {
        super("Associate already voted.");
    }
}
