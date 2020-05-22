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
    private final Agenda DEFAULT_AGENDA = new Agenda("1", "Default description.", AgendaStatus.CLOSED);

    @HystrixCommand(fallbackMethod = "defaultAgendas")
    public List<Agenda> getAllAgendas() {
        return agendaRepository.getAllAgendas();
    }

    @HystrixCommand(
            fallbackMethod = "defaultGetAgendaById",
            ignoreExceptions = { AgendaNotFoundException.class })
    public Agenda getAgendaById(String id) throws AgendaNotFoundException {
        return Optional.ofNullable(agendaRepository.getAgendaById(id))
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));
    }

    @HystrixCommand(fallbackMethod = "defaultAddAgenda")
    public Agenda addAgenda(Agenda agenda) {
        return agendaRepository.saveAgenda(agenda);
    }

    @HystrixCommand(
            fallbackMethod = "defaultOpenAgenda",
            ignoreExceptions = { AgendaNotFoundException.class })
    public Agenda openAgenda(OpenedAgendaDTO openedAgendaDTO) throws AgendaNotFoundException {
        String id = openedAgendaDTO.getId();
        Agenda agenda = Optional.ofNullable(agendaRepository.getAgendaById(id))
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));

        agenda.setStartDate(openedAgendaDTO.getStartDate());
        agenda.setStatus(openedAgendaDTO.getStatus());

        return agendaRepository.saveAgenda(agenda);
    }

    @HystrixCommand(
            fallbackMethod = "defaultCloseAgenda",
            ignoreExceptions = { AgendaNotFoundException.class })
    public Agenda closeAgenda(ClosedAgendaDTO closedAgendaDTO) throws AgendaNotFoundException {
        String id = closedAgendaDTO.getId();
        Agenda agenda = Optional.ofNullable(agendaRepository.getAgendaById(id))
                .orElseThrow(() -> new AgendaNotFoundException("Voting agenda not found. ID: " + id));

        agenda.setVotes(closedAgendaDTO.getVotes());
        agenda.setEndDate(closedAgendaDTO.getEndDate());
        agenda.setStatus(closedAgendaDTO.getStatus());

        return agendaRepository.saveAgenda(agenda);
    }

    public List<Agenda> defaultAgendas() {
        return new ArrayList<>();
    }

    public Agenda defaultGetAgendaById(String id) {
        return DEFAULT_AGENDA;
    }

    public Agenda defaultAddAgenda(Agenda agenda) {
        return DEFAULT_AGENDA;
    }

    public Agenda defaultOpenAgenda(OpenedAgendaDTO openedAgendaDTO) {
        return DEFAULT_AGENDA;
    }

    public Agenda defaultCloseAgenda(ClosedAgendaDTO closedAgendaDTO) {
        return DEFAULT_AGENDA;
    }
}
