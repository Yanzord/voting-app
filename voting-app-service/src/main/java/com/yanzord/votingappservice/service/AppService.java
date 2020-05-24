package com.yanzord.votingappservice.service;

import com.yanzord.votingappservice.dto.AgendaDTO;
import com.yanzord.votingappservice.dto.AgendaStatus;
import com.yanzord.votingappservice.dto.SessionDTO;
import com.yanzord.votingappservice.dto.VoteDTO;
import com.yanzord.votingappservice.exception.ClosedAgendaException;
import com.yanzord.votingappservice.exception.OpenedAgendaException;
import com.yanzord.votingappservice.exception.UnknownAgendaStatusException;
import com.yanzord.votingappservice.feign.VotingAgendaClient;
import com.yanzord.votingappservice.feign.VotingSessionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppService {
    @Autowired
    private VotingAgendaClient votingAgendaClient;
    @Autowired
    private VotingSessionClient votingSessionClient;

    public AgendaDTO registerAgenda(AgendaDTO agenda) {
        return votingAgendaClient.registerAgenda(agenda);
    }

    public SessionDTO openSession(SessionDTO session) throws OpenedAgendaException, ClosedAgendaException, UnknownAgendaStatusException {
        AgendaDTO agenda = votingAgendaClient.getAgendaById(session.getAgendaId());

        switch(agenda.getStatus()) {
            case NEW: {
                LocalDateTime startDate = LocalDateTime.now();

                agenda.setStatus(AgendaStatus.OPENED);
                agenda.setStartDate(startDate);
                votingAgendaClient.updateAgenda(agenda);

                session.setStartDate(startDate);
                return votingSessionClient.openSession(session);
            }
            case OPENED: {
                throw new OpenedAgendaException("Agenda is opened, register a vote with id: " + agenda.getId());
            }
            case CLOSED: {
                throw new ClosedAgendaException("Agenda is closed, see the results with id: " + agenda.getId());
            }
            default: {
                throw new UnknownAgendaStatusException("Unknown agenda status: " + agenda.getStatus());
            }
        }
    }

    public SessionDTO registerVote(VoteDTO vote, String agendaId) {


        return votingSessionClient.registerVote(vote, agendaId);
    }

    public AgendaDTO getAgendaResult(String agendaId) {
        return votingAgendaClient.getAgendaById(agendaId);
    }
}
