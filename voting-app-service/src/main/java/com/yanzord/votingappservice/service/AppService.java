package com.yanzord.votingappservice.service;

import com.yanzord.votingappservice.dto.*;
import com.yanzord.votingappservice.exception.*;
import com.yanzord.votingappservice.feign.VotingAgendaClient;
import com.yanzord.votingappservice.feign.VotingSessionClient;
import feign.FeignException;
import org.json.JSONObject;
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

    public AgendaDTO registerAgenda(AgendaDTO agenda) {
        agenda.setStatus(AgendaStatus.NEW);
        return votingAgendaClient.registerAgenda(agenda);
    }

    public SessionDTO createSession(SessionDTO session) throws SessionException {
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

            return votingSessionClient.createSession(session);
        }

        throw new SessionException();
    }

    public SessionDTO registerVote(VoteDTO vote, String agendaId) throws FinishedAgendaException, VoteException, ClosedSessionException {
        SessionDTO session = votingSessionClient.getSessionByAgendaId(agendaId);

        if (LocalDateTime.now().isAfter(session.getEndDate())) {
            session.setStatus(SessionStatus.CLOSED);

            AgendaDTO agenda = votingAgendaClient.getAgendaById(agendaId);

            if (agenda.getStatus() == AgendaStatus.FINISHED) {
                throw new FinishedAgendaException();
            }

            AgendaResult agendaResult = generateAgendaResult(session);

            agenda.setAgendaResult(agendaResult);
            agenda.setStatus(AgendaStatus.FINISHED);
            votingAgendaClient.updateAgenda(agenda);

            throw new ClosedSessionException("Session is closed.");
        }

        List<VoteDTO> votes = Optional.ofNullable(session.getVotes())
                .orElse(new ArrayList<>());

        if (votes.stream().anyMatch(v -> v.getAssociateId().equals(vote.getAssociateId()))) {
            throw new VoteException("Associate already voted.");
        }

        votes.add(vote);
        session.setVotes(votes);

        return votingSessionClient.updateSession(session);
    }

    public AgendaResult getAgendaResult(String agendaId) throws NewAgendaException {
        AgendaDTO agenda = votingAgendaClient.getAgendaById(agendaId);

        if (agenda.getStatus().equals(AgendaStatus.NEW)) {
            throw new NewAgendaException();
        }

        return agenda.getAgendaResult();
    }

    public AgendaResult generateAgendaResult(SessionDTO session) {
        List<VoteDTO> votes = session.getVotes();

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
}
