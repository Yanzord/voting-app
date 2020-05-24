package com.yanzord.votingappservice.service;

import com.yanzord.votingappservice.dto.AgendaDTO;
import com.yanzord.votingappservice.feign.VotingAgendaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppService {
    @Autowired
    private VotingAgendaClient votingAgendaClient;

    public AgendaDTO registerAgenda(AgendaDTO agendaDTO) {
        return votingAgendaClient.addAgenda(agendaDTO);
    }
}
