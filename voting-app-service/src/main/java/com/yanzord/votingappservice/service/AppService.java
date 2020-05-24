package com.yanzord.votingappservice.service;

import com.yanzord.votingappservice.dto.AgendaDTO;
import com.yanzord.votingappservice.feign.VotingAgendaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppService {
    @Autowired
    private VotingAgendaClient votingAgendaClient;

    public AgendaDTO registerAgenda(AgendaDTO agendaDTO) {
        return votingAgendaClient.registerAgenda(agendaDTO);
    }

    public List<AgendaDTO> getAllAgendas() {
        return votingAgendaClient.getAllAgendas();
    }
}
