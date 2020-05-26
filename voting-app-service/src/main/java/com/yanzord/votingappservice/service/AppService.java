package com.yanzord.votingappservice.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yanzord.votingappservice.model.*;
import com.yanzord.votingappservice.exception.*;
import com.yanzord.votingappservice.feign.CPFValidator;
import com.yanzord.votingappservice.feign.VotingAgendaClient;
import com.yanzord.votingappservice.feign.VotingSessionClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppService {
    @Autowired
    private VotingAgendaClient votingAgendaClient;
    @Autowired
    private VotingSessionClient votingSessionClient;
    @Autowired
    private CPFValidator cpfValidator;

    public Agenda registerAgenda(Agenda agenda) {
        agenda.setStatus(AgendaStatus.NEW);
        return votingAgendaClient.saveAgenda(agenda);
    }

    public Session createSession(Session session) throws CreatedSessionException {
        try {
            votingSessionClient.getSessionByAgendaId(session.getAgendaId());
        } catch (FeignException.NotFound e) {
            LocalDateTime startDate = LocalDateTime.now();
            session.setStartDate(startDate);

            LocalDateTime endDate = session.getStartDate().plusMinutes(session.getDuration());
            session.setEndDate(endDate);
            session.setStatus(SessionStatus.OPENED);

            if (session.getDuration() == 0)
                session.setDuration(1);

            return votingSessionClient.saveSession(session);
        }

        throw new CreatedSessionException();
    }

    public Session registerVote(Vote vote, String agendaId) throws VoteException, ClosedSessionException, InvalidCpfException {
        Session session = votingSessionClient.getSessionByAgendaId(agendaId);

        if (LocalDateTime.now().isAfter(session.getEndDate())) {
            closeSession(session);
            Agenda agenda = votingAgendaClient.getAgendaById(agendaId);
            finishAgenda(agenda, generateAgendaResult(session));

            throw new ClosedSessionException();
        }

        List<Vote> votes = Optional.ofNullable(session.getVotes())
                .orElse(new ArrayList<>());

        if (votes.stream()
                .anyMatch(v -> v.getAssociateId().equals(vote.getAssociateId()) || v.getAssociateCPF().equals(vote.getAssociateCPF()))) {
            throw new VoteException();
        }

        try {
            ObjectNode json = cpfValidator.validateCPF(vote.getAssociateCPF());

            VoteStatus status = VoteStatus.valueOf(json.get("status").asText());

            if(status.equals(VoteStatus.UNABLE_TO_VOTE))
                throw new InvalidCpfException("Associate is unable to vote.");

            votes.add(vote);
            session.setVotes(votes);

            return votingSessionClient.saveSession(session);
        } catch(FeignException.NotFound e) {
            throw new InvalidCpfException("Invalid CPF.");
        }
    }

    public AgendaResult getAgendaResult(String agendaId) throws NewAgendaException {
        Agenda agenda = votingAgendaClient.getAgendaById(agendaId);

        if(agenda.getAgendaResult() == null) {
            if(agenda.getStatus().equals(AgendaStatus.NEW)) {
                Session session = votingSessionClient.getSessionByAgendaId(agendaId);

                if(session.getStatus().equals(SessionStatus.CLOSED)) {
                    return finishAgenda(agenda, generateAgendaResult(session));
                }

                if(session.getStatus().equals(SessionStatus.OPENED)) {
                    if (LocalDateTime.now().isAfter(session.getEndDate())) {
                        closeSession(session);
                        return finishAgenda(agenda, generateAgendaResult(session));
                    }

                    throw new NewAgendaException();
                }

                throw new NewAgendaException();
            }
        }

        return agenda.getAgendaResult();
    }

    public AgendaResult generateAgendaResult(Session session) {
        List<Vote> votes = Optional.ofNullable(session.getVotes())
                .orElse(new ArrayList<>());

        long totalUpVotes = votes.stream()
                .filter(vote -> vote.getVoteChoice().equals(VoteChoice.SIM))
                .count();

        long totalDownVotes = votes.stream()
                .filter(vote -> vote.getVoteChoice().equals(VoteChoice.NAO))
                .count();

        if (totalUpVotes > totalDownVotes) {
            String result = VoteChoice.SIM.toString();

            return new AgendaResult(totalUpVotes, totalDownVotes, result);
        }

        if (totalDownVotes > totalUpVotes) {
            String result = VoteChoice.NAO.toString();

            return new AgendaResult(totalUpVotes, totalDownVotes, result);
        }

        String result = "EMPATE";

        return new AgendaResult(totalUpVotes, totalDownVotes, result);
    }

    public AgendaResult finishAgenda(Agenda agenda, AgendaResult agendaResult) {
        agenda.setAgendaResult(agendaResult);
        agenda.setStatus(AgendaStatus.FINISHED);
        votingAgendaClient.saveAgenda(agenda);

        return agendaResult;
    }

    public void closeSession(Session session) {
        session.setStatus(SessionStatus.CLOSED);
        votingSessionClient.saveSession(session);
    }
}
