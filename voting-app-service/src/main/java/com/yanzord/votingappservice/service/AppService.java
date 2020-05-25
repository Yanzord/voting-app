package com.yanzord.votingappservice.service;

import com.yanzord.votingappservice.dto.*;
import com.yanzord.votingappservice.exception.FinishedAgendaException;
import com.yanzord.votingappservice.exception.ClosedSessionException;
import com.yanzord.votingappservice.exception.UnknownAgendaStatusException;
import com.yanzord.votingappservice.feign.VotingAgendaClient;
import com.yanzord.votingappservice.feign.VotingSessionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppService {
    @Autowired
    private VotingAgendaClient votingAgendaClient;
    @Autowired
    private VotingSessionClient votingSessionClient;

    public AgendaDTO registerAgenda(AgendaDTO agenda) {
        return votingAgendaClient.registerAgenda(agenda);
    }

    public SessionDTO openSession(SessionDTO session) throws FinishedAgendaException, UnknownAgendaStatusException {
        AgendaDTO agenda = votingAgendaClient.getAgendaById(session.getAgendaId());

        switch(agenda.getStatus()) {
            case NEW: {
                LocalDateTime startDate = LocalDateTime.now();
                session.setStartDate(startDate);

                return votingSessionClient.openSession(session);
            }
            case FINISHED: {
                throw new FinishedAgendaException("Agenda is finished, can't open session.");
            }
            default: {
                throw new UnknownAgendaStatusException("Unknown agenda status.");
            }
        }
    }

    public SessionDTO registerVote(VoteDTO vote, String agendaId) throws ClosedSessionException, FinishedAgendaException {
        SessionDTO sessionDTO = votingSessionClient.registerVote(vote, agendaId);

        if(sessionDTO.getStatus().equals(SessionStatus.CLOSED)) {
            AgendaDTO agenda = votingAgendaClient.getAgendaById(agendaId);

            switch(agenda.getStatus()) {
                case NEW: {
                    AgendaResult agendaResult = calculateAgendaResult(sessionDTO);

                    agenda.setAgendaResult(agendaResult);
                    agenda.setStatus(AgendaStatus.FINISHED);
                    votingAgendaClient.updateAgenda(agenda);
                }
                case FINISHED: {
                    throw new FinishedAgendaException("Agenda is finished and session is closed, can't register vote.");
                }
            }

            throw new ClosedSessionException("Session is closed.");
        }

        return sessionDTO;
    }

    public AgendaResult getAgendaResult(String agendaId) {
        return votingAgendaClient.getAgendaById(agendaId).getAgendaResult();
    }

    public AgendaResult calculateAgendaResult(SessionDTO session) {
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

        if(totalDownVotes > totalUpVotes) {
            String result = VoteChoice.NAO.toString();

            return new AgendaResult(totalUpVotes, totalDownVotes, result);
        }

        String result = "EMPATE";

        return new AgendaResult(totalUpVotes, totalDownVotes, result);
    }
}
