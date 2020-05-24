package com.yanzord.votingappservice.service;

import com.yanzord.votingappservice.dto.AgendaDTO;
import com.yanzord.votingappservice.dto.SessionDTO;
import com.yanzord.votingappservice.dto.VoteDTO;
import com.yanzord.votingappservice.feign.VotingAgendaClient;
import com.yanzord.votingappservice.feign.VotingSessionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppService {
    @Autowired
    private VotingAgendaClient votingAgendaClient;
    @Autowired
    private VotingSessionClient votingSessionClient;

    public AgendaDTO registerAgenda(AgendaDTO agenda) {
        return votingAgendaClient.registerAgenda(agenda);
    }

    public SessionDTO openSession(SessionDTO session) {
        return votingSessionClient.openSession(session);
    }

    public SessionDTO registerVote(VoteDTO vote, String sessionId) {
        return votingSessionClient.registerVote(vote, sessionId);
    }

    public AgendaDTO getAgendaResult(String agendaId) {
        return votingAgendaClient.getAgendaById(agendaId);
    }
}
