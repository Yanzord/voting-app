package com.yanzord.votingagendaservice.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yanzord.votingagendaservice.dto.ClosedAgendaDTO;
import com.yanzord.votingagendaservice.dto.OpenedAgendaDTO;
import com.yanzord.votingagendaservice.exception.AgendaNotFoundException;
import com.yanzord.votingagendaservice.model.AgendaStatus;
import com.yanzord.votingagendaservice.model.Agenda;
import com.yanzord.votingagendaservice.repository.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AgendaService {
    @Autowired
    private AgendaRepository agendaRepository;

    @HystrixCommand(fallbackMethod = "defaultAgendas")
    public List<Agenda> getAllAgendas() {
        return agendaRepository.getAllAgendas();
    }

    @HystrixCommand(fallbackMethod = "defaultAgenda")
    public Agenda getAgendaById(String id) {
        return Optional.ofNullable(agendaRepository.getAgendaById(id))
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));
    }

    @HystrixCommand(fallbackMethod = "defaultAgenda")
    public Agenda addAgenda(Agenda agenda) {
        return agendaRepository.saveAgenda(agenda);
    }

    @HystrixCommand(fallbackMethod = "defaultAgenda")
    public Agenda openAgenda(OpenedAgendaDTO openedAgendaDTO) {
        String id = openedAgendaDTO.getId();
        Agenda agenda = Optional.ofNullable(agendaRepository.getAgendaById(id))
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));

        agenda.setStartDate(openedAgendaDTO.getStartDate());
        agenda.setStatus(openedAgendaDTO.getStatus());

        return agendaRepository.saveAgenda(agenda);
    }

    @HystrixCommand(fallbackMethod = "defaultAgenda")
    public Agenda closeAgenda(ClosedAgendaDTO closedAgendaDTO) {
        String id = closedAgendaDTO.getId();
        Agenda agenda = Optional.ofNullable(agendaRepository.getAgendaById(id))
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));

        agenda.setVotes(closedAgendaDTO.getVotes());
        agenda.setEndDate(closedAgendaDTO.getEndDate());
        agenda.setStatus(closedAgendaDTO.getStatus());

        return agendaRepository.saveAgenda(agenda);
    }

    public Agenda defaultAgenda() {
        return new Agenda("1", "Default agenda.", AgendaStatus.CLOSED);
    }

    public List<Agenda> defaultAgendas() {
        return new ArrayList<>();
    }
}
