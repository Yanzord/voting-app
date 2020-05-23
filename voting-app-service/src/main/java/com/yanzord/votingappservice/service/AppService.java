package com.yanzord.votingappservice.service;

import com.yanzord.votingappservice.dto.AgendaDTO;
import com.yanzord.votingappservice.feign.VotingAgendaClient;
import org.springframework.beans.factory.annotation.Autowired;

public class AppService {
    @Autowired
    private VotingAgendaClient votingAgendaClient;

    public AgendaDTO createAgenda(AgendaDTO agendaDTO) {
        return votingAgendaClient.addAgenda(agendaDTO);
    }
}
